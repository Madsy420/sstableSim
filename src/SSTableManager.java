import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class SSTableManager {

    /**
     * Used to get a value from the SSTable
     * @param partitionKey
     * @param dataKey
     * @return
     */
    public static String getFromSSTable(String partitionKey, String dataKey)
    {
        int fileLevelToOpen = 1;
        while(true)
        {
            //Set the file path
            String ssTableFilePath = SSTConstants.FILE_PATH + SSTConstants.FILE_CONSTANT + fileLevelToOpen +
                    SSTConstants.JSON_EXTENSION;
            File ssTableFile = new File(ssTableFilePath);
            try
            {
                //If we reach the this level, and it doesn't exist, the value doesn't exist in the SSTable
                if(!ssTableFile.exists())
                {
                    break;
                }
                ObjectMapper mapper = new ObjectMapper();

                //Reading the ssTableFile(stored as JSON) and mapping it to the ssTable object
                List<SSTable> ssTablesInFile = Arrays.asList(mapper.readValue(ssTableFile, SSTable[].class));

                //If the sstable list is empty, it means there are no values stored, and we can not fetch any value
                if(ssTablesInFile.isEmpty())
                {
                    break;
                }

                //This part deals with searching through the string to get the value we need
                for(int ssTableNum = ssTablesInFile.size()-1; ssTableNum >=0; ssTableNum--)
                {
                    SSTable ssTable = ssTablesInFile.get(ssTableNum);`

                    String[] splitPartitions = ssTable.getData().split("[" +
                            SSTConstants.PARTITION_KEY_VALUE_PAIR_SEPARATOR + "]");
                    for(String partition : splitPartitions)
                    {
                        String[] partitionKeyValSplit = partition.split("[" +
                                SSTConstants.PARTITION_KEY_VALUE_SEPARATOR + "]");
                        String tempPartitionKey = partitionKeyValSplit[0];
                        if(tempPartitionKey.equals(partitionKey))
                        {
                            String[] dataKeyValPairs = partitionKeyValSplit[1].split("[" +
                                    SSTConstants.DATA_KEY_VALUE_PAIR_SEPARATOR + "]");
                            for(String dataKeyVal : dataKeyValPairs)
                            {
                                String[] splitDataKeyVal = dataKeyVal.split("[" +
                                        SSTConstants.DATA_KEY_VALUE_SEPARATOR + "]");
                                String tempDataKey = splitDataKeyVal[0];
                                if(tempDataKey.equals(dataKey))
                                {
                                    String val = splitDataKeyVal[1];
                                    if(val.equals(SSTConstants.TOMBSTONE))
                                    {
                                        return null;
                                    }
                                    return val;
                                }
                            }
                        }
                    }
                }
            }
            catch(Exception e)
            {
                return null;
            }
            fileLevelToOpen++;
        }
        return null;
    }

    static int maxAllowedSSTableAtLevel(int level)
    {
        return (int)Math.pow(5, level);
    }

    static SSTable mergeSSTables(List<SSTable> ssTableList)
    {
        List<String> ssTableDataList = ssTableList.stream().map(ssTable -> ssTable.getData()).collect(Collectors.toList());
        TreeMap<String, TreeMap<String, String>> mergedSSTableDataTree = new TreeMap<>();

        for(String ssTableString : ssTableDataList)
        {
            String[] splitPartitionKeyValPairs = ssTableString.split(SSTConstants.PARTITION_KEY_VALUE_PAIR_SEPARATOR);
            for(String partitionKeyValPair : splitPartitionKeyValPairs)
            {
                String[] splitKeyVal = partitionKeyValPair.split(SSTConstants.PARTITION_KEY_VALUE_SEPARATOR);
                String partitionKey = splitKeyVal[0];
                String[] splitDataKeyValPairs = splitKeyVal[1].split(SSTConstants.DATA_KEY_VALUE_SEPARATOR);
                for(String dataKeyValPair : splitDataKeyValPairs)
                {
                    String[] splitDataKeyVal = dataKeyValPair.split(SSTConstants.DATA_KEY_VALUE_SEPARATOR);
                    String key = splitDataKeyVal[0];
                    String val = splitDataKeyVal[1];
                    if(val.equals(SSTConstants.TOMBSTONE))
                    {
                        if(mergedSSTableDataTree.containsKey(partitionKey) &&
                            mergedSSTableDataTree.get(partitionKey).containsKey(key))
                        {
                            mergedSSTableDataTree.get(partitionKey).remove(key);
                            if (mergedSSTableDataTree.get(partitionKey).isEmpty())
                            {
                                mergedSSTableDataTree.remove(partitionKey);
                            }
                        }
                    }
                    else
                    {
                        if(mergedSSTableDataTree.containsKey(partitionKey))
                        {
                            mergedSSTableDataTree.get(partitionKey).put(key, val);
                        }
                        else
                        {
                            mergedSSTableDataTree.put(partitionKey, new TreeMap<>());
                            mergedSSTableDataTree.get(partitionKey).put(key, val);
                        }
                    }
                }
            }

        }

        return new SSTable(convertDataToString(mergedSSTableDataTree));
    }

    public static void flushMemTable(TreeMap<String, TreeMap<String, String>> memTable)
    {
        FlushThread flushTread = new FlushThread((TreeMap<String, TreeMap<String, String>>)memTable.clone());
        flushTread.start();
        memTable.clear();
    }

    static String convertDataToString(TreeMap<String, TreeMap<String, String>> dataTree)
    {
        StringBuilder tempDataStr = new StringBuilder();
        for(String partitionKey : dataTree.descendingKeySet())
        {
            TreeMap<String, String> partitionTreeMap = dataTree.get(partitionKey);
            tempDataStr.append(partitionKey).append(SSTConstants.PARTITION_KEY_VALUE_SEPARATOR);
            boolean firstKey = true;
            for (String key : partitionTreeMap.descendingKeySet())
            {
                if (!firstKey)
                {
                    tempDataStr.append(SSTConstants.DATA_KEY_VALUE_PAIR_SEPARATOR);
                }
                else
                {
                    firstKey = false;
                }
                tempDataStr.append(key).append(SSTConstants.DATA_KEY_VALUE_SEPARATOR).append(dataTree.get(key));
            }
            tempDataStr.append(SSTConstants.PARTITION_KEY_VALUE_PAIR_SEPARATOR);
        }
        return tempDataStr.toString();
    }

    public static class FlushThread extends Thread {

        TreeMap<String, TreeMap<String, String>> dataTree;
        FlushThread(TreeMap<String, TreeMap<String, String>> dataTree)
        {
            this.dataTree = dataTree;
        }
        public void run() {
            SSTable ssTable = new SSTable(SSTableManager.convertDataToString(dataTree));

            int fileLevelToOpen = 1;

            while (true) {
                String ssTableFilePath = SSTConstants.FILE_PATH + SSTConstants.FILE_CONSTANT + fileLevelToOpen +
                        SSTConstants.JSON_EXTENSION;;
                File ssTableFile = new File(ssTableFilePath);

                try {
                    if (!ssTableFile.exists()) {
                        ssTableFile.createNewFile();
                    }

                    ObjectMapper mapper = new ObjectMapper();

                    List<SSTable> ssTablesInFile = Arrays.asList(mapper.readValue(ssTableFile, SSTable[].class));
                    if (ssTablesInFile.size() >= SSTableManager.maxAllowedSSTableAtLevel(fileLevelToOpen)) {
                        ssTable = SSTableManager.mergeSSTables(ssTablesInFile);
                        mapper.writeValue(ssTableFile, new ArrayList<>());
                        fileLevelToOpen++;
                    } else {
                        ssTablesInFile.add(ssTable);
                        mapper.writeValue(ssTableFile, ssTablesInFile);
                        break;
                    }

                } catch (Exception e) {
                    break;
                }

            }

        }
    }

}




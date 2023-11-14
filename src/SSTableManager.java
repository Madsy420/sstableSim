import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class SSTableManager {

    //This function loads and returns metadata of a SSTable file that can be used to get keys from later
    public static synchronized SSTableCacheObject loadSSTable(String filePath) throws IOException {
        try (RandomAccessFile file = new RandomAccessFile(filePath, "r")) {

            // First * bytes of the SStable includes the Data block length and number of entries both as Ints(4+4=8)
            int totalDataBlockByteLength = file.readInt();
            int numEntries = file.readInt();

            // Skip the data blocks and move to the index block
            file.skipBytes(totalDataBlockByteLength);

            // Read the index block, iterating through it and storing the key offsets vs the key
            int indexSize = file.readInt();
            TreeMap<String, Long> indexTable = new TreeMap<>();
            for (int i = 0; i < indexSize; i++) {
                int keyLength = file.readInt();
                byte[] keyBytes = new byte[keyLength];
                file.readFully(keyBytes);
                long offset = file.readLong();

                String indexedKey = new String(keyBytes, StandardCharsets.UTF_8);
                indexTable.put(indexedKey, offset);
            }

            // Read the Bloom filter
            int bloomFilterSize = file.readInt();
            byte[] bloomFilterBytes = new byte[bloomFilterSize];
            file.readFully(bloomFilterBytes);
            BitSet bloomFilter = BitSet.valueOf(bloomFilterBytes);

            //storing the information into the object, that can be used to quickly access the SSTable shown by the path
            SSTableCacheObject ssTableCacheObject = new SSTableCacheObject(numEntries, filePath, bloomFilter, indexTable);
            return ssTableCacheObject;
        }
    }

    private static synchronized void convertToSSTable(TreeMap<String, String> data) throws IOException {
        String filePath = getNewSStableNamePath();
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(filePath))) {

            // Write the key and value lengths to the SSTable,
            int totalByteLength = 0;

            for(Map.Entry<String, String> entry : data.entrySet())
            {
                String key = entry.getKey();
                String value = entry.getValue();

                byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
                byte[] valueBytes = value.getBytes(StandardCharsets.UTF_8);
                // the "4" signifies the length added while entering the key and value "lengths" before the key and
                // value themselves, and the lengths are in "int" which uses 4 bytes.
                totalByteLength += 4 + keyBytes.length + 4 + valueBytes.length;
            }

            //we are adding the total byte length of the key_value pairs we are about to store including their length
            dos.writeInt(totalByteLength);

            //we write the total number of keys
            dos.writeInt(data.size());

            long offset = dos.size();

            //initiate bloomfilter
            int bloomFilterSize = data.size();
            BitSet bloomFilter = new BitSet(bloomFilterSize);

            //initiate index
            TreeMap<String, Long> index = new TreeMap<>();

            for (Map.Entry<String, String> entry : data.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                // Write the key and value to the SSTable
                byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
                byte[] valueBytes = value.getBytes(StandardCharsets.UTF_8);

                //entering in key_length;key;value_length:value format(without the ":")
                dos.writeInt(keyBytes.length);
                dos.write(keyBytes);
                dos.writeInt(valueBytes.length);
                dos.write(valueBytes);

                //a simple bloomfilter entry
                bloomFilter.set(Math.abs(key.hashCode() % bloomFilterSize), true);

                // Add key and offset to the index block
                index.put(key, offset);

                // end of current input is taken as the offset, which will later be stored in the index as entry point
                // of the next key
                offset = dos.size();
            }

            // write the number of entries in the index
            dos.writeInt(index.size());
            for (Map.Entry<String, Long> indexEntry : index.entrySet()) {
                String indexedKey = indexEntry.getKey();
                long indexOffset = indexEntry.getValue();

                byte[] indexedKeyBytes = indexedKey.getBytes(StandardCharsets.UTF_8);

                //writing value in key_length:key:value_offset format(without the ":")
                dos.writeInt(indexedKeyBytes.length);
                dos.write(indexedKeyBytes);
                dos.writeLong(indexOffset);
            }

            //finally write the bloomfilter
            byte[] bloomFilterBytes = bloomFilter.toByteArray();
            dos.writeInt(bloomFilterBytes.length);
            dos.write(bloomFilterBytes);

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Merges all the SSTables in the root folder.
     * @throws IOException
     */
    public static synchronized void mergeSSTables() throws IOException {
        // Map to store the merged data
        TreeMap<String, String> mergedData = new TreeMap<>();
        List<String> sstableFilePaths = new ArrayList<>();

        sstableFilePaths.addAll(getSSTablePaths(false));

        // Merge data from each SSTable
        for (String sstableFilePath : sstableFilePaths) {
            try (DataInputStream dis = new DataInputStream(new FileInputStream(sstableFilePath))) {
                // Skip the data block
                int totalDataBlockByteLength = dis.readInt();
                int numEntries = dis.readInt();

                /*
                // Skip the Bloom filter
                int bloomFilterSize = dis.readInt();
                dis.skipBytes(bloomFilterSize);
                 */

                // Read key-value pairs directly
                for(int entryIter = 0; entryIter < numEntries; entryIter++) {
                    int keyLength = dis.readInt();
                    byte[] keyBytes = new byte[keyLength];
                    dis.readFully(keyBytes);
                    int valueLength = dis.readInt();
                    byte[] valueBytes = new byte[valueLength];
                    dis.readFully(valueBytes);

                    String key = new String(keyBytes, StandardCharsets.UTF_8);
                    String value = new String(valueBytes, StandardCharsets.UTF_8);

                    // Add the key-value pair to the merged data
                    if(value.equals(SSTableConstants.TOMB_STONE))
                    {
                        if(mergedData.containsKey(key))
                        {
                            mergedData.remove(key);
                        }
                    }
                    else
                    {
                        mergedData.put(key, value);
                    }
                }
            }
        }
        deleteSSTables(sstableFilePaths);

        // Write the merged data to a new SSTable file
        convertToSSTable(mergedData);
        try {
            Thread.sleep(50*mergedData.size());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static synchronized void deleteSSTables(List<String> paths)
    {
        for(String ssTablePath : paths){
            File file = new File(ssTablePath);

            // Check if the file exists
            if (file.exists()) {
                // Attempt to delete the file
                if (!file.delete()) {
                    System.err.println("Failed to delete the file: " + ssTablePath);
                }
            }
            else {
                System.err.println("File " + ssTablePath + " does not exist.");
            }
        }
    }

    private static synchronized boolean checkAndMergeSSTable()
    {
        try
        {
            if (getNumOfSSTables() >= SSTableConstants.MAX_SSTABLE_COUNT)
            {
                mergeSSTables();
                return true;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public static synchronized boolean flushToSSTable(TreeMap<String, String> memTable)
    {
        boolean merged = checkAndMergeSSTable();
        try
        {
            convertToSSTable(memTable);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return merged;
    }

    /**
     * Returns all the SSTable file paths in the SSTABLE_ROOT_FOLDER, in an ordered manner, latest to oldest or v.v
     * depending on the flag passed(we currently name the files in such a way that the later ones can be easily
     * sorted to the front, since we use the time of creation as part of the file name)
     * @param latestToOldest
     * @return
     */
    public static synchronized List<String> getSSTablePaths(boolean latestToOldest)
    {
        try
        {
            List<String> filesPathList = new ArrayList<>();
            File ssTableDirectory = new File(SSTableConstants.SSTABLE_ROOT_FOLDER);

            File[] ssTables = ssTableDirectory.listFiles();
            if(latestToOldest)
            {
                filesPathList.addAll(
                        Arrays.stream(ssTables).map(ssTable -> ssTable.getPath()).sorted(Comparator.reverseOrder()).
                                collect(Collectors.toList())
                );
            }
            else
            {
                filesPathList.addAll(
                        Arrays.stream(ssTables).map(ssTable -> ssTable.getPath()).sorted().
                                collect(Collectors.toList())
                );
            }
            return filesPathList;

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static synchronized int getNumOfSSTables()
    {
        File ssTableDirectory = new File(SSTableConstants.SSTABLE_ROOT_FOLDER);
        return ssTableDirectory.listFiles().length;
    }

    private static synchronized String getNewSStableNamePath()
    {
        LocalDateTime now = LocalDateTime.now();

        // Format the date and time as per your desired format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy_MM_dd_HH_mm_ss");
        String formattedDateTime = now.format(formatter);

        // Generate the file path using the formatted date and time
        String fileName = "sstable_" + formattedDateTime + ".dat";
        String filePath = SSTableConstants.SSTABLE_ROOT_FOLDER + "/" + fileName;
        return filePath;
    }
}

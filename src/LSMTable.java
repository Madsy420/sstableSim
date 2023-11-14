import java.util.List;
import java.util.TreeMap;

public class LSMTable {

    private TreeMap<String, String> memTable;
    private TreeMap<String, SSTableCacheObject> ssTableCacheObjectList = new TreeMap<>();

    LSMTable()
    {
        memTable = new TreeMap<>();
    }

    /**
     * Takes care of addition of new value to the LSM Table, adds it to the memtable, and flushes the table
     * before adding the value if required
     * @param key
     * @param value
     */
    public synchronized void put(String key, String value)
    {
        boolean verbose = true;
        try
        {
            // if the memTable has reached its maximum length flush it into a SSTable
            if (memTable.size() >= SSTableConstants.MAX_MEMTABLE_SIZE)
            {
                boolean merged = SSTableManager.flushToSSTable(memTable);


                // we also clear the SSTable stored in cache, since there might be newer version of the data in the
                // recently created SSTable, so next time we fetch a value we have to load the latest SSTable and check.
                loadAllSSTablesToCache();

                // clear the memTable
                memTable.clear();

                if(verbose) {
                    Thread.sleep(1000);
                    System.out.println("++++++++++++++++++++++++++++++++");
                    System.out.println("SSTable after latest flush:");
                    System.out.println("No of SSTables:" + ssTableCacheObjectList.size());
                    System.out.println("Did merge occur: " + merged);
                    ssTableCacheObjectList.forEach((keyI, valueI) -> {
                        System.out.println("____________________");
                        System.out.println("SSTable:" + keyI);
                        System.out.println("Index Table: ");
                        System.out.println(valueI.indexTable);
                    });
                    System.out.println("++++++++++++++++++++++++++++++++");
                }
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        memTable.put(key, value);
        if(verbose)
        {
            System.out.println("======================================================");
            System.out.println("Mem table after the Newest entry:");
            System.out.println("Mem table length: " + memTable.size());
            System.out.println(memTable);
            System.out.println("======================================================");
        }

    }

    /**
     * checks the information in the memTable, If found, return value, else check the SSTables,
     * returns null if still not found
     * @param key
     * @return
     */
    public synchronized String get(String key)
    {
        if(memTable.containsKey(key))
        {
            return memTable.get(key);
        }
        String val = fetchValForKeyFromSSTable(key);
        if(val == null || val.equals(SSTableConstants.TOMB_STONE)){
            return null;
        }
        return val;
    }

    /**
     * Tries to return a value from SSTable if it finds any, otherwise return null, refreshes the SSTable in cache if
     * required
     * @param key
     * @return
     */
    private synchronized String fetchValForKeyFromSSTable(String key)
    {
        String val = null;

        // if a SSTable is not available in cache, load one,and read
        if(ssTableCacheObjectList.isEmpty())
        {
            loadAllSSTablesToCache();
        }

        for(String path : ssTableCacheObjectList.descendingKeySet())
        {
            SSTableCacheObject ssTableCacheObject = ssTableCacheObjectList.get(path);
            val = ssTableCacheObject.getVal(key);
            if(val != null)
            {
                break;
            }
        }

        return val;
    }

    /**
     * Used to load all SSTables to cache, this is a dummy placeholder for a better way to cache the
     * important SSTables, determined by some metric.
     * @return
     */
    private synchronized void loadAllSSTablesToCache()
    {
        boolean verbose = true;
        if(!ssTableCacheObjectList.isEmpty())
        {
            ssTableCacheObjectList.clear();
        }
        try
        {
            List<String> getAllSSTablePaths = SSTableManager.getSSTablePaths(true);

            for (String path : getAllSSTablePaths)
            {
                SSTableCacheObject ssTableCacheObject = SSTableManager.loadSSTable(path);
                ssTableCacheObjectList.put(path, ssTableCacheObject);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}

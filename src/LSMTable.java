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
    public void put(String key, String value)
    {
        try
        {
            // if the memTable has reached its maximum length flush it into a SSTable
            if (memTable.size() > SSTableConstants.MAX_MEMTABLE_SIZE)
            {
                SSTableManager.flushToSSTable(memTable);

                // we also clear the SSTable stored in cache, since there might be newer version of the data in the
                // recently created SSTable, so next time we fetch a value we have to load the latest SSTable and check.
                loadAllSSTablesToCache();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        // clear the memTable and add the newest value
        memTable.clear();
        memTable.put(key, value);
    }

    /**
     * checks the information in the memTable, If found, return value, else check the SSTables,
     * returns null if still not found
     * @param key
     * @return
     */
    public String get(String key)
    {
        if(memTable.containsKey(key))
        {
            return memTable.get(key);
        }

        return fetchValForKeyFromSSTable(key);
    }

    /**
     * Tries to return a value from SSTable if it finds any, otherwise return null, refreshes the SSTable in cache if
     * required
     * @param key
     * @return
     */
    public String fetchValForKeyFromSSTable(String key)
    {
        String val = null;

        // if a SSTable is not available in cache, load one,and read
        if(ssTableCacheObjectList.isEmpty())
        {
            loadAllSSTablesToCache();
        }
        else
        {
            for(String path : ssTableCacheObjectList.descendingKeySet())
            {
                SSTableCacheObject ssTableCacheObject = ssTableCacheObjectList.get(path);
                val = ssTableCacheObject.getVal(key);
                if(val != null)
                {
                    break;
                }
            }
        }

        return val;
    }

    /**
     * Used to load a SSTable into cache, the SSTable loaded is the one with the latest copy of the value.
     * returns value if such a SSTable is found, otherwise returns null.
     * @return
     */
    private void loadAllSSTablesToCache()
    {
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

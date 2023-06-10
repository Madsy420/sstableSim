import java.util.TreeMap;

public class LSMTable {
    private static final int MAX_MEMTABLE_SIZE = 20;
    private TreeMap<String, String> memTable;

    public void put(String key, String value)
    {
        try
        {
            if (memTable.size() > MAX_MEMTABLE_SIZE)
            {
                SSTableManager.flushToSSTable(memTable);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        memTable.clear();
        memTable.put(key, value);
    }

    public String get(String key)
    {
        if(memTable.containsKey(key))
        {
            return memTable.get(key);
        }

        return SSTableManager.fetchValForKey(key);
    }


}

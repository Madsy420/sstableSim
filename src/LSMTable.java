import java.util.TreeMap;

public class LSMTable {
    public final int memTableSizeLimit = 50;
    TreeMap<String, TreeMap<String, String>> memTable = new TreeMap<>();
    int memTableSize = 0;

    LSMTable()
    {

    }

    public void put(String partitionKey, String dataKey, String val)
    {
        if(memTableSize > memTableSizeLimit)
        {
            SSTableManager.flushMemTable(memTable);
            memTableSize = 0;
        }
        addToMemTable(partitionKey, dataKey, val);
        memTableSize++;
    }

    public String get(String partitionKey, String dataKey)
    {
        if(memTable.containsKey(partitionKey) &&
           memTable.get(partitionKey).containsKey(dataKey))
        {
            return memTable.get(partitionKey).get(dataKey);
        }
        else
        {
            return SSTableManager.getFromSSTable(partitionKey, dataKey);
        }
    }

    private void addToMemTable(String partitionKey, String dataKey, String val)
    {
        if(!memTable.containsKey(partitionKey))
        {
            memTable.put(partitionKey, new TreeMap<>());
        }
        memTable.get(partitionKey).put(dataKey, val);
    }

    public int getMemTableSize()
    {

    }

}

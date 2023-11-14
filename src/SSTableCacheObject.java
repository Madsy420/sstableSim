import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import java.util.TreeMap;

public class SSTableCacheObject {
    int numEntries;
    String filePath;
    BitSet bloomFilter;
    TreeMap<String, Long> indexTable;
    SSTableCacheObject(int numEntries, String filePath, BitSet bloomFilter, TreeMap<String, Long> indexTable)
    {
        this.numEntries = numEntries;
        this.filePath = filePath;
        this.bloomFilter = bloomFilter;
        this.indexTable = indexTable;
    }

    public String getVal(String key)
    {
        if (bloomFilter.get(Math.abs(key.hashCode() % numEntries)))
        {
            Long indexToStartFrom = getIndexToStartFrom(key);
            if(indexToStartFrom != null)
            {
                try (RandomAccessFile file = new RandomAccessFile(filePath, "r")){
                    file.seek(indexToStartFrom);
                    int fetchedKeyLength = file.readInt();
                    byte[] fetchedKeyBytes = new byte[fetchedKeyLength];
                    file.readFully(fetchedKeyBytes);
                    int fetchedValueLength = file.readInt();
                    byte[] fetchedValueBytes = new byte[fetchedValueLength];
                    file.readFully(fetchedValueBytes);

                    String fetchedKey = new String(fetchedKeyBytes, StandardCharsets.UTF_8);
                    String fetchedValue = new String(fetchedValueBytes, StandardCharsets.UTF_8);
                    return fetchedValue;
                }
                catch (IOException e)
                {
                    System.out.println(key);
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private Long getIndexToStartFrom(String key)
    {
        if(indexTable.containsKey(key))
        {
            return indexTable.get(key);
        }
        return null;
    }
}

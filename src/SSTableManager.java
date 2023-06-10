import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class SSTableManager {

    private static final int MAX_SSTABLE_COUNT = 5;
    private static final String SSTABLE_ROOT_FOLDER = "ROOT//FOLDER//PATH";
    private static String fetchValueFromFile(String key, String filePath) throws IOException {
        try (RandomAccessFile file = new RandomAccessFile(filePath, "r")) {
            int totalDataBlockByteLength = file.readInt();
            int numEntries = file.readInt();

            // Skip the data blocks and move to the index block
            file.skipBytes(totalDataBlockByteLength);

            // Read the index block
            int indexSize = file.readInt();
            TreeMap<String, Long> index = new TreeMap<>();
            for (int i = 0; i < indexSize; i++) {
                int keyLength = file.readInt();
                byte[] keyBytes = new byte[keyLength];
                file.readFully(keyBytes);
                long offset = file.readLong();

                String indexedKey = new String(keyBytes, StandardCharsets.UTF_8);
                index.put(indexedKey, offset);
            }

            // Read the Bloom filter
            int bloomFilterSize = file.readInt();
            byte[] bloomFilterBytes = new byte[bloomFilterSize];
            file.readFully(bloomFilterBytes);
            BitSet bloomFilter = BitSet.valueOf(bloomFilterBytes);

            // Check if the key may exist in the SSTable based on the Bloom filter
            if (bloomFilter.get(Math.abs(key.hashCode() % numEntries))) {

                // Check if the key exists in the index
                if (index.containsKey(key)) {
                    long offset = index.get(key);

                    // Move to the data block using the offset (seek backward)
                    file.seek(offset);

                    // Read the key-value pair
                    int fetchedKeyLength = file.readInt();
                    byte[] fetchedKeyBytes = new byte[fetchedKeyLength];
                    file.readFully(fetchedKeyBytes);
                    int fetchedValueLength = file.readInt();
                    byte[] fetchedValueBytes = new byte[fetchedValueLength];
                    file.readFully(fetchedValueBytes);

                    String fetchedKey = new String(fetchedKeyBytes, StandardCharsets.UTF_8);
                    String fetchedValue = new String(fetchedValueBytes, StandardCharsets.UTF_8);

                    return fetchedValue;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    private static void convertToSSTable(TreeMap<String, String> data) throws IOException {
        String filePath = getNewSStableNamePath();
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(filePath))) {

            int totalByteLength = 0;

            for(Map.Entry<String, String> entry : data.entrySet())
            {
                String key = entry.getKey();
                String value = entry.getValue();

                // Write the key and value to the SSTable
                byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
                byte[] valueBytes = value.getBytes(StandardCharsets.UTF_8);
                totalByteLength += 4 + keyBytes.length + 4 + valueBytes.length;
            }

            dos.writeInt(totalByteLength);
            dos.writeInt(data.size());

            long offset = dos.size();
            int bloomFilterSize = data.size();
            BitSet bloomFilter = new BitSet(bloomFilterSize);
            TreeMap<String, Long> index = new TreeMap<>();

            for (Map.Entry<String, String> entry : data.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                // Write the key and value to the SSTable
                byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
                byte[] valueBytes = value.getBytes(StandardCharsets.UTF_8);

                dos.writeInt(keyBytes.length);
                dos.write(keyBytes);
                dos.writeInt(valueBytes.length);
                dos.write(valueBytes);

                bloomFilter.set(Math.abs(key.hashCode() % bloomFilterSize), true);

                // Add key and offset to the index block
                index.put(key, offset);

                offset = dos.size();
            }

            dos.writeInt(index.size());
            for (Map.Entry<String, Long> indexEntry : index.entrySet()) {
                String indexedKey = indexEntry.getKey();
                long indexOffset = indexEntry.getValue();

                byte[] indexedKeyBytes = indexedKey.getBytes(StandardCharsets.UTF_8);

                dos.writeInt(indexedKeyBytes.length);
                dos.write(indexedKeyBytes);
                dos.writeLong(indexOffset);
            }

            byte[] bloomFilterBytes = bloomFilter.toByteArray();
            dos.writeInt(bloomFilterBytes.length);
            dos.write(bloomFilterBytes);

        }
    }

    public static void mergeSSTables() throws IOException {
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

                // Skip the Bloom filter
                int bloomFilterSize = dis.readInt();
                dis.skipBytes(bloomFilterSize);

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
                    mergedData.put(key, value);
                }
            }
        }

        // Write the merged data to a new SSTable file
        convertToSSTable(mergedData);
    }

    private static void checkAndMergeSSTable()
    {
        try
        {
            if (getNumOfSSTables() >= MAX_SSTABLE_COUNT)
            {
                mergeSSTables();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void flushToSSTable(TreeMap<String, String> memTable)
    {
        checkAndMergeSSTable();
        try
        {
            convertToSSTable(memTable);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static String fetchValForKey(String key)
    {
        List<String> getAllSSTablePaths = getSSTablePaths(true);

        try
        {
            for (String path : getAllSSTablePaths)
            {
                String val = fetchValueFromFile(key, path);
                if (val != null)
                {
                    return val;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static List<String> getSSTablePaths(boolean latestToOldest)
    {
        try
        {
            List<String> filesPathList = new ArrayList<>();
            File ssTableDirectory = new File(SSTABLE_ROOT_FOLDER);

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

    public static int getNumOfSSTables()
    {
        File ssTableDirectory = new File(SSTABLE_ROOT_FOLDER);
        return ssTableDirectory.listFiles().length;
    }

    private static String getNewSStableNamePath()
    {
        LocalDateTime now = LocalDateTime.now();

        // Format the date and time as per your desired format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy_MM_dd_HH_mm_ss");
        String formattedDateTime = now.format(formatter);

        // Generate the file path using the formatted date and time
        String fileName = "sstable_" + formattedDateTime + ".dat";
        String filePath = SSTABLE_ROOT_FOLDER + "\\" + fileName;
        return fileName;
    }
}

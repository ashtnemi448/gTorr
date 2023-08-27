package gtorr.Util;

import gtorr.GTorrApplication;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class Utils {
    public static <T> T getRandomElementFromHashSet(HashSet<T> set) {
        if (set.isEmpty()) {
            throw new IllegalArgumentException("HashSet is empty");
        }
        T[] array = (T[]) set.toArray();
        Random random = new Random();
        int randomIndex = random.nextInt(array.length);
        return array[randomIndex];
    }

    public static Long getNumberOfChunks(String file) throws IOException {
        Path path = Path.of(file);
        Long numOfChunks = (long) Math.ceil((double) Files.size(path) / GTorrApplication.s_chunkSize);
        return numOfChunks;
    }


    static HashMap<String,RandomAccessFile> fileHashMap = new HashMap<>();
    public synchronized static byte[] getChunk(int offset, String fileName) throws IOException {

        if(!fileHashMap.containsKey(fileName)){
            fileHashMap.put(fileName,new RandomAccessFile(fileName, "r"));
        }
        RandomAccessFile file = fileHashMap.get(fileName);

        int chunkToRead = GTorrApplication.s_chunkSize;
        if(offset + GTorrApplication.s_chunkSize >= file.length()){
//            System.out.println("Hello");
            chunkToRead = (int) (file.length() - offset);
//            System.out.println("Chunk rem "  + chunkToRead);
        }
        file.seek(offset);

        byte[] chunk = new byte[chunkToRead];
        file.read(chunk);
        return chunk;
    }
}

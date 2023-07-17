package gtorr.Util;

import gtorr.GTorrApplication;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
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
        byte[] fileData = Files.readAllBytes(path);
        Long numOfChunks = (long) Math.ceil((double) fileData.length / GTorrApplication.s_chunkSize);
        return numOfChunks;
    }

    public static byte[] getChunk(int offset, String fileName) throws IOException {

        RandomAccessFile file = new RandomAccessFile(fileName, "r");

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

package gtorr.Util;

import gtorr.GTorrApplication;

import java.io.IOException;
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
        return array[0];
    }

    public static Long  getNumberOfChunks(String file) throws IOException {
        Path path = Path.of(file);
        byte[] fileData = Files.readAllBytes(path);
        Long numOfChunks = (long) Math.ceil((double) fileData.length / GTorrApplication.s_chunkSize);
        return numOfChunks;
    }

}

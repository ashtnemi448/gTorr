package gtorr.Util;

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
}

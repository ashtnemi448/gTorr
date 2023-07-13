package gtorr.Util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Random;

public class HashUtils {

    public static String concatenateHashes(String leftHash, String rightHash) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        String concatString = leftHash+ rightHash;

        byte[] hash = md.digest(concatString.getBytes());
        return HashUtils.bytesToHex(hash);
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static <T> T getRandomObject(HashSet<T> hashSet) {
        if (hashSet.isEmpty()) {
            return null;
        }

        int randomIndex = new Random().nextInt(hashSet.size());
        T[] array = (T[]) hashSet.toArray();
        return array[randomIndex];
    }
    
}

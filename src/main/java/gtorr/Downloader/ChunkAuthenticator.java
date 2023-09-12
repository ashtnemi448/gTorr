package gtorr.Downloader;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class ChunkAuthenticator {

    public static boolean checkIfChunkIsSane(List<ValidityHash> validityHashes, String file, byte[] chunk, String fileRoot) throws NoSuchAlgorithmException, IOException {
        if (validityHashes.size() == 0) return true;
        String actualRoot = PersistentMerkelTree.getHash(new String(chunk));

        String vRoot = "";
        for (ValidityHash validityHash : validityHashes) {
            vRoot = validityHash.getHash();
            if (validityHash == null && fileRoot.equals(actualRoot)) {
                break;
            }
            String combinedHash;
            if (validityHash.getIsLeft() == 0) {
                combinedHash = PersistentMerkelTree.getHash(actualRoot + vRoot);
            } else {
                combinedHash = PersistentMerkelTree.getHash(vRoot + actualRoot);
            }
            actualRoot = combinedHash;
        }
        return fileRoot.equals(actualRoot);
    }
}

package gtorr.Downloader;

import gtorr.Util.HashUtils;

import java.security.NoSuchAlgorithmException;
import java.util.List;

public class ChunkAuthenticator {

    public static boolean checkIfChunkIsSane(List<ValidityHash> validityHashes, MerkleNode node, MerkleNode root) throws NoSuchAlgorithmException {
        MerkleNode currNode = node;
        for (ValidityHash validityHash : validityHashes) {
            if (validityHash != null && validityHash.getHash().equals(root.getHash())) {
                break;
            }
            String parentHash = "";
            String combinedHash;
            if (validityHash.getIsLeft() == 0) {
                combinedHash = HashUtils.concatenateHashes(currNode.getHash(), validityHash.getHash());
            } else {
                combinedHash = HashUtils.concatenateHashes(validityHash.getHash(), currNode.getHash());
            }
            parentHash = combinedHash;
            currNode = new MerkleNode(parentHash);
        }
        return currNode.getHash().equals(root.getHash());
    }
}

package gtorr;

import java.security.NoSuchAlgorithmException;
import java.util.List;

public class ChunkAuthenticator {
    MerkleNode mRoot;

    public ChunkAuthenticator(MerkleNode root) {
        mRoot = root;
    }


    boolean checkIfChunkIsSane(List<ValidityHash> validityHash, MerkleNode node) throws NoSuchAlgorithmException {
        MerkleNode currNode = node;
        for (ValidityHash x : validityHash) {
            if (x!=null && x.getHash().equals(mRoot.getHash())) {
                break;
            }
            String parentHash = "";
            String combinedHash;
            if (x.getIsLeft() == 0)
                combinedHash = HashUtils.concatenateHashes(currNode.getHash(), x.getHash());
            else
                combinedHash = HashUtils.concatenateHashes(x.getHash(), currNode.getHash());
            parentHash = combinedHash;
            currNode = new MerkleNode(parentHash);
        }
        return currNode.getHash().equals(mRoot.getHash());
    }

}

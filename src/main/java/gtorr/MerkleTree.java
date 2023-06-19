package gtorr;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

class MerkleNode implements Serializable {
    private String mHash;
    private byte[] mChunkBytes;
    private MerkleNode mLeft;
    private MerkleNode mRight;
    private MerkleNode mParent;

    public MerkleNode(){}

    public MerkleNode(byte[] chunkBytes,String hash) {
        this.mChunkBytes = chunkBytes;
        this.mHash = hash;
        this.mLeft = null;
        this.mRight = null;
        this.mParent = null;
    }

    public MerkleNode(String hash) {
        this.mHash = hash;
        this.mLeft = null;
        this.mRight = null;
        this.mParent = null;
    }
    private int mIsLeft;

    public int getIsLeft() {
        return mIsLeft;
    }

    public byte[] getChunkBytes(){
        return mChunkBytes;
    }

    public void setIsLeft(int isLeft) {
        this.mIsLeft = isLeft;
    }

    public String getHash() {
        return mHash;
    }

    public MerkleNode getLeft() {
        return mLeft;
    }

    public MerkleNode getParent() {
        return mParent;
    }

    public void setLeft(MerkleNode left) {
        this.mLeft = left;
    }

    public MerkleNode getRight() {
        return mRight;
    }

    public void setRight(MerkleNode right) {
        this.mRight = right;
    }

    public void setParent(MerkleNode parent) {
        this.mParent = parent;
    }
}

public class MerkleTree {
    private MerkleNode mRoot;
    private int mChunkSize;
    List<MerkleNode> mLeafNodes;
    private List<MerkleNode>  mLastLeaf ;

    public MerkleTree(String filePath, int chunkSize) throws IOException, NoSuchAlgorithmException {
        this.mLastLeaf = new ArrayList<>();;
        this.mChunkSize = chunkSize;
        this.mRoot = buildTreeFromFile(filePath);
    }

    public List<MerkleNode> getLeaves() {
        return mLeafNodes;
    }

    public MerkleNode getRoot() {
        return mRoot;
    }

    private MerkleNode getBrother(MerkleNode node) {
        if (node == null) return null;
        MerkleNode parent = node.getParent();
        if (parent == null) return null;
        MerkleNode left = parent.getLeft();
        MerkleNode right = parent.getRight();

        if (left.getHash().equals(node.getHash())) return right;
        return left;
    }

    private MerkleNode getUncle(MerkleNode node) {
        MerkleNode parent = node.getParent();
        return getBrother(parent);
    }

    private MerkleNode getParent(MerkleNode current/*, MerkleNode node*/) {
        if (current == null) return null;
        return current.getParent();
    }

    private MerkleNode buildTreeFromFile(String filePath) throws IOException, NoSuchAlgorithmException {
        List<byte[]> fileChunks = divideFileIntoChunks(filePath);
        mLeafNodes = createLeafNodes(fileChunks);

        List<MerkleNode> leaves = mLeafNodes;

        while (leaves.size() > 1) {
            List<MerkleNode> parents = new ArrayList<>();
            for (int i = 0; i < leaves.size(); i += 2) {
                MerkleNode parent;
                MerkleNode left = leaves.get(i);
                MerkleNode right = (i + 1 < leaves.size()) ? leaves.get(i + 1) : null;

                if (right == null){
                    mLastLeaf.add(left);
                    continue;
                }
                else
                    parent = createParentNode(left, right);
                parents.add(parent);
            }
            leaves = parents;
        }

        if (leaves.size() == 1)
            mRoot = leaves.get(0);

        for(int i = mLastLeaf.size()-1;i>=0;i--){
            mRoot = createParentNode(mRoot,mLastLeaf.get(i));
        }
        return mRoot;
    }

    private MerkleNode createParentNode(MerkleNode left, MerkleNode right) throws NoSuchAlgorithmException {
        String combinedHash = HashUtils.concatenateHashes(left.getHash(), right.getHash());
        MerkleNode parent = new MerkleNode(combinedHash);

        parent.setLeft(left);
        parent.setRight(right);

        left.setIsLeft(1);
        left.setParent(parent);

        right.setParent(parent);
        right.setIsLeft(0);

        return parent;
    }

    private List<byte[]> divideFileIntoChunks(String filePath) throws IOException {
        Path path = Path.of(filePath);
        byte[] fileData = Files.readAllBytes(path);
        int chunkSize = mChunkSize; // Set your desired chunk size
        int numOfChunks = (int) Math.ceil((double) fileData.length / chunkSize);
        List<byte[]> chunks = new ArrayList<>();

        for (int i = 0; i < numOfChunks; i++) {
            int start = i * chunkSize;
            int length = Math.min(chunkSize, fileData.length - start);
            byte[] chunk = new byte[length];
            System.arraycopy(fileData, start, chunk, 0, length);
            chunks.add(chunk);
        }

        return chunks;
    }

    public List<MerkleNode> createLeafNodes(List<byte[]> chunks) throws NoSuchAlgorithmException {
        List<MerkleNode> leafNodes = new ArrayList<>();
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        for (byte[] chunk : chunks) {
            byte[] hash = md.digest(chunk);
            leafNodes.add(new MerkleNode(chunk,HashUtils.bytesToHex(hash)));
        }

        return leafNodes;
    }

    public void printNodeDetails(MerkleNode node) {
        if (node != null) {
            System.out.println("Node: " + node.getHash());

            MerkleNode brother = getBrother(node);
            if (brother != null)
                System.out.println("Brother: " + brother.getHash());
            else
                System.out.println("No brother exists for this node.");

            MerkleNode uncle = getUncle(node);
            if (uncle != null)
                System.out.println("Uncle: " + uncle.getHash());
            else
                System.out.println("No uncle exists for this node.");

            System.out.println();
        }
    }

    void print(MerkleNode node) throws NoSuchAlgorithmException {
        if (node == null) return;
        print(node.getLeft());
        List<ValidityHash> x = getValidityHash(node);

        boolean v = verifyTree(x, node);
        if (v)
            System.out.println("SANE ");
        else
            System.out.println("INSANE ");
        print(node.getRight());
    }

    public List<ValidityHash> getValidityHash(MerkleNode node) {
        List<ValidityHash> validityHashes = new ArrayList<>();
        if (getBrother(node) != null)
            validityHashes.add(new ValidityHash(getBrother(node).getHash(),getBrother(node).getIsLeft()));

        while (getParent(node) != null) {
            MerkleNode uncle = getUncle(node);
            if (uncle != null)
                validityHashes.add(new ValidityHash(uncle.getHash(),uncle.getIsLeft()));
            node = uncle;
        }
        return validityHashes;
    }

    boolean verifyTree(List<ValidityHash> validityHash, MerkleNode node) throws NoSuchAlgorithmException {
        MerkleNode currNode = node;

        for (ValidityHash x : validityHash) {
            if (x.getHash().equals(mRoot.getHash())) {
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

    /*================================= TESTING ================================ */
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        MerkleTree merkleTree = new MerkleTree("r.mp4", 1000000);
        List<MerkleNode> leaves = merkleTree.getLeaves();
        for (int i = 0; i < leaves.size(); i++) {
            if (!merkleTree.verifyTree(merkleTree.getValidityHash(leaves.get(i)),leaves.get(i))) {
                System.out.println("Invalid Chunk " + i);
            } else {
                System.out.println("Valid Chunk " + i);
            }
        }
    }
}

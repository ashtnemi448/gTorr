package gtorr.Downloader;

import java.io.Serializable;

public class MerkleNode implements Serializable {
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
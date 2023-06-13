package gtorr;

import java.util.Arrays;
import java.util.List;

class RequestParams{
    long chunkId;
    String bytesHash;

    public double getChunkId() {
        return chunkId;
    }

    public byte[] chunk;

    public byte[] getChunk() {
        return chunk;
    }

    @Override
    public String toString() {
        return "RequestParams{" +
                "chunkId=" + chunkId +
                ", bytesHash='" + bytesHash + '\'' +
                ", validityHashList=" + validityHashList +
                '}';
    }

    public void setChunk(byte[] chunk) {
        this.chunk = chunk;
    }

    public void setChunkId(long chunkId) {
        this.chunkId = chunkId;
    }

    public String getBytesHash() {
        return bytesHash;
    }

    public void setBytesHash(String bytesHash) {
        this.bytesHash = bytesHash;
    }

    public List<String> getValidityHashList() {
        return validityHashList;
    }

    public void setValidityHashList(List<String> validityHashList) {
        this.validityHashList = validityHashList;
    }

    List<String> validityHashList;
}
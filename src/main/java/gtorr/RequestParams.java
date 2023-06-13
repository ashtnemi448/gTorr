package gtorr;

import java.util.List;

class RequestParams{
    long chunkId;
    String bytesHash;

    public double getChunkId() {
        return chunkId;
    }

    @Override
    public String toString() {
        return "RequestParams{" +
                "chunkId=" + chunkId +
                ", bytesHash='" + bytesHash + '\'' +
                ", validityHashList=" + validityHashList +
                '}';
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
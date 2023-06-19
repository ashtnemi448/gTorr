package gtorr;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

class ResponseParam {
    String mHash;
    public byte[] mChunk;

    @JsonProperty
    List<ValidityHash> mValidityHashList;

    public byte[] getChunk() {
        return mChunk;
    }

    @Override
    public String toString() {
        return "RequestParams{" + ", bytesHash='" + mHash + '\'' + ", validityHashList=" + mValidityHashList + '}';
    }

    public void setChunk(byte[] chunk) {
        this.mChunk = chunk;
    }

    public String getHash() {
        return mHash;
    }

    public void setHash(String hash) {
        this.mHash = hash;
    }

    public List<ValidityHash> getValidityHashList() {
        return mValidityHashList;
    }

    public void setValidityHashList(List<ValidityHash> validityHashList) {
        this.mValidityHashList = validityHashList;
    }
}
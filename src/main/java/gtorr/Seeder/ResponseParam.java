package gtorr.Seeder;

import com.fasterxml.jackson.annotation.JsonProperty;
import gtorr.Downloader.ValidityHash;

import java.util.List;

public class ResponseParam {
    String mHash;
    public byte[] mChunk;

    private String mRootHash;
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

    public String getRootHash() {
        return mRootHash;
    }

    public void setRootHash(String rootHash) {
        this.mRootHash = rootHash;
    }

    public void setValidityHashList(List<ValidityHash> validityHashList) {
        this.mValidityHashList = validityHashList;
    }

}
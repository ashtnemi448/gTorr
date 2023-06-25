package gtorr.Downloader;

import java.io.Serializable;

public class ValidityHash implements Serializable {
    public String mHash;
    public int mIsLeft;

    public ValidityHash(){}

    public String getHash() {
        return mHash;
    }

    public void setHash(String hash) {
        this.mHash = hash;
    }

    public int getIsLeft() {
        return mIsLeft;
    }

    public void setIsLeft(int isLeft) {
        this.mIsLeft = isLeft;
    }

    public ValidityHash(String hash, int isLeft) {
        this.mHash = hash;
        this.mIsLeft = isLeft;
    }
}

package gtorr;

import java.io.Serializable;

public class ValidityHash implements Serializable {
    public String hash;
    public int isLeft;

    public ValidityHash(){}

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getIsLeft() {
        return isLeft;
    }

    public void setIsLeft(int isLeft) {
        this.isLeft = isLeft;
    }

    public ValidityHash(String hash, int isLeft) {
        this.hash = hash;
        this.isLeft = isLeft;
    }
}

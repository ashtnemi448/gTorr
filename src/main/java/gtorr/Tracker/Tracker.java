package gtorr.Tracker;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Entity
public class Tracker {

    @Id
    private String mFileHash;

    @Column
    private HashSet<String> mFileNames = new HashSet<>();

    @Column
    Long mFileSize;

    @Column
    private HashSet<String> mHosts = new HashSet<>();

    public void setFileNames(HashSet<String> fileNames) {
        this.mFileNames = fileNames;
    }

    public Long getFileSize() {
        return mFileSize;
    }

    public void setFileSize(Long fileSize) {
        this.mFileSize = fileSize;
    }

    public HashSet<String> getFileNames() {
        return mFileNames;
    }

    public void setFileName(HashSet<String> fileNames) {
        this.mFileNames = fileNames;
    }

    public String getFileHash() {
        return mFileHash;
    }

    public HashSet<String> getHosts() {
        return mHosts;
    }

    @Override
    public String toString() {
        return "Tracker{" +
                "mFileHash='" + mFileHash + '\'' +
                ", mFileNames=" + mFileNames +
                ", mFileSize=" + mFileSize +
                ", mHosts=" + mHosts +
                '}';
    }

    public void setFileHash(String fileCkSum) {
        this.mFileHash = fileCkSum;
    }

    public void setHosts(HashSet<String> hosts) {
        this.mHosts = hosts;
    }
}

package gtorr.Downloader;

public class RequestParam {
    private int mChunkId;
    private String mFileHash;
    private String mHost;
    private String mFileName;

    public String getFileName() {
        return mFileName;
    }

    public void setFileName(String fileName) {
        this.mFileName = fileName;
    }

    public int getChunkId() {
        return mChunkId;
    }

    public void setChunkId(int chunkId) {
        this.mChunkId = chunkId;
    }

    public String getFileHash() {
        return mFileHash;
    }

    public void setFileHash(String fileCkSum) {
        this.mFileHash = fileCkSum;
    }

    public String getHost() {
        return mHost;
    }

    public void setHost(String host) {
        this.mHost = host;
    }
    
    @Override
    public String toString() {
        return "RequestParam{" + "mChunkId=" + mChunkId + ", mFileHash=" + mFileHash + ", mHost='" + mHost + '\'' + '}';
    }
}

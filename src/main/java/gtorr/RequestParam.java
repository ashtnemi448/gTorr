package gtorr;

public class RequestParam {
    private int chunkId;
    private String filename;

    public int getChunkId() {
        return chunkId;
    }

    @Override
    public String  toString() {
        return "gtorr.RequestParam{" +
                "chunkId=" + chunkId +
                ", filename='" + filename + '\'' +
                '}';
    }

    public void setChunkId(int chunkId) {
        this.chunkId = chunkId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}

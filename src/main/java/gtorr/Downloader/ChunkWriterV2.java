package gtorr.Downloader;

import gtorr.GTorrApplication;
import gtorr.Seeder.ResponseParam;
import org.springframework.data.util.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class ChunkWriterV2 {

    HashMap<String, RandomAccessFile> mFileHashMap = new HashMap<>();
    RandomAccessFile mWriteFile;
    ResponseParam mResponseParam;
    RequestParam mRequestParam;
    List<RequestParam> mFailedDownload;


    public ChunkWriterV2(ResponseParam responseParam, RequestParam requestParam, List<RequestParam> failedDownload) throws FileNotFoundException {
        if (mFileHashMap.get(requestParam.getFileName()) == null) {
            mFileHashMap.put(requestParam.getFileName(), new RandomAccessFile(new File("Torrent-" + requestParam.getFileName()), "rw"));
        }
        this.mWriteFile = mFileHashMap.get(requestParam.getFileName());
        this.mResponseParam = responseParam;
        this.mRequestParam = requestParam;
        this.mFailedDownload = failedDownload;
    }

    public synchronized void writeChunk() throws NoSuchAlgorithmException {
        if (!ChunkAuthenticator.checkIfChunkIsSane(mResponseParam.getValidityHashList(), new MerkleNode(mResponseParam.getHash()), new MerkleNode(mResponseParam.getRootHash()))) {
            System.out.println("Invalid Chunk " + mRequestParam.getChunkId());
            mFailedDownload.add(mRequestParam);
            return;
        }

        try {
            mWriteFile.seek((long) mRequestParam.getChunkId() * GTorrApplication.s_chunkSize);
            mWriteFile.write(mResponseParam.mChunk);
        } catch (Exception ignored) {
        }
    }

}
// abc def ghi jkl mno
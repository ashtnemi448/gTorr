package gtorr.Downloader;

import gtorr.GTorrApplication;
import gtorr.Seeder.ResponseParam;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class ChunkWriter {

    HashMap<String, RandomAccessFile> mFileHashMap = new HashMap<>();
    RandomAccessFile mWriteFile;
    ResponseParam mResponseParam;
    RequestParam mRequestParam;
    List<RequestParam> mFailedDownload;


    public ChunkWriter(ResponseParam responseParam, RequestParam requestParam, List<RequestParam> failedDownload) throws FileNotFoundException {
        if (mFileHashMap.get(requestParam.getFileName()) == null) {
            mFileHashMap.put(requestParam.getFileName(), new RandomAccessFile(new File("Torrent-" + requestParam.getFileName()), "rw"));
        }
        this.mWriteFile = mFileHashMap.get(requestParam.getFileName());
        this.mResponseParam = responseParam;
        this.mRequestParam = requestParam;
        this.mFailedDownload = failedDownload;
    }
    long start = System.currentTimeMillis();

    public synchronized void writeChunk() throws NoSuchAlgorithmException, IOException {
        start = System.currentTimeMillis();
        if (!ChunkAuthenticator.checkIfChunkIsSane(mResponseParam.getValidityHashList() , mRequestParam.getFileName() , mResponseParam.getChunk(), mRequestParam.getFileHash())) {
            System.out.println("Invalid Chunk " + mRequestParam.getChunkId());
            mFailedDownload.add(mRequestParam);
            return;
        }

        try {
            long start = System.currentTimeMillis();
            mWriteFile.seek((long) mRequestParam.getChunkId() * GTorrApplication.s_chunkSize);
            mWriteFile.write(mResponseParam.mChunk);
            long end = System.currentTimeMillis();
            float sec = (end - start) ;
            System.out.println("Time taken for write "+  mRequestParam.getChunkId() + "  chunk " + sec + " ms");
        } catch (Exception ignored) {
        }
    }

}
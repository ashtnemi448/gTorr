package gtorr.Downloader;

import gtorr.Seeder.ResponseParam;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChunkWriterV2 {

    HashMap<String, RandomAccessFile> mFileHashMap = new HashMap<>();
    RandomAccessFile mWriteFile;
    ResponseParam mResponseParam;
    RequestParam mRequestParam;

    public ChunkWriterV2(ResponseParam responseParam, RequestParam requestParam) throws FileNotFoundException {
        if (mFileHashMap.get(requestParam.getFileName()) == null) {
            mFileHashMap.put(requestParam.getFileName(), new RandomAccessFile(new File(requestParam.getFileName()), "rw"));
        }
        this.mWriteFile = mFileHashMap.get(requestParam.getFileName());
        this.mResponseParam = responseParam;
        this.mRequestParam = requestParam;
    }

    public void writeChunk() throws NoSuchAlgorithmException {

        if (!ChunkAuthenticator.checkIfChunkIsSane(mResponseParam.getValidityHashList(), new MerkleNode(mResponseParam.getHash()), new MerkleNode(mResponseParam.getRootHash()))) {
            System.out.println("Invalid Chunk " + mRequestParam.getChunkId());
            return;
        } else {
            System.out.println("Valid Chunk " + mRequestParam.getChunkId());
        }

        synchronized (mWriteFile) {
            try {
                mWriteFile.seek(mRequestParam.getChunkId());
                mWriteFile.write(mResponseParam.mChunk);
            } catch (Exception ignored) {
            }
        }
    }
}
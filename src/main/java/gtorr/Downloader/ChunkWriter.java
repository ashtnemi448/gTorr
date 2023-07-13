package gtorr.Downloader;

import gtorr.GTorrApplication;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChunkWriter implements Runnable {
    static int s_chunkSize = GTorrApplication.s_chunkSize;
    int mChunkId;
    RandomAccessFile mWriteFile;
    byte[] mChunk;

    public ChunkWriter(int chunkId, RandomAccessFile m_writeFile, byte[] chunk) {
        this.mChunkId = chunkId;
        this.mWriteFile = m_writeFile;
        this.mChunk = chunk;
    }

    @Override
    public void run() {
        synchronized (mWriteFile) {
            try {

                mWriteFile.seek(mChunkId);
                mWriteFile.write(mChunk);

            } catch (Exception ignored) {
            }
        }
    }

    /**
     * For Testing
     * =============================================++++++++++=============++++++++============+++++++++++============
     */

    static public MerkleTree seederMerkleTree;
    static public MerkleNode seederRoot;
    static public List<MerkleNode> seederLeaves;
    static public Queue<Integer> seederChunkIdQueue = new LinkedList<>();
    static public long seederFileLength;


    static public void seeder() throws IOException, NoSuchAlgorithmException {

        seederMerkleTree = new MerkleTree("r.mp4", s_chunkSize);
        RandomAccessFile r = new RandomAccessFile(new File("r.mp4"), "rw");

//        seederMerkleTree = new MerkleTree("read.txt", s_chunkSize);
//        RandomAccessFile r = new RandomAccessFile(new File("read.txt"), "rw");

        seederFileLength = r.length();

        seederRoot = seederMerkleTree.getRoot();
        seederLeaves = seederMerkleTree.getLeaves();

        //Mimic shuffling
        ArrayList<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < seederLeaves.size(); i++) {
            numbers.add(i);
        }
        Collections.shuffle(numbers);
        seederChunkIdQueue.addAll(numbers);
    }

    static class InputParams {
        public int chunkId;
        public String chunkHash;
        public byte[] chunk;

        public MerkleNode node;
        public List<ValidityHash> validityHashes;

        public InputParams(int chunkId, String chunkHash, byte[] chunk, List<ValidityHash> validityHashes, MerkleNode node) {
            this.chunkId = chunkId;
            this.chunkHash = chunkHash;
            this.chunk = chunk;
            this.validityHashes = validityHashes;
            this.node = node;
        }
    }

    static public InputParams getRandomChunk() {
        InputParams params = null;
        if (seederChunkIdQueue.size() == 0) return params;

        Integer currChunkId = seederChunkIdQueue.poll();
        params = new InputParams(currChunkId,
                seederLeaves.get(currChunkId).getHash(),
                seederLeaves.get(currChunkId).getChunkBytes(),
                seederMerkleTree.getValidityHash(seederLeaves.get(currChunkId)),
                seederLeaves.get(currChunkId));

        return params;
    }

    public static void main(String[] args) throws IOException, InterruptedException, NoSuchAlgorithmException {
        ExecutorService executor = Executors.newFixedThreadPool(12);

        RandomAccessFile w = new RandomAccessFile(new File("w.mp4"), "rw");
//        RandomAccessFile w = new RandomAccessFile(new File("write.txt"), "rw");
        w.setLength(seederFileLength);

        seeder();

        int currChunk = 0;
        while (true) {
            InputParams params = getRandomChunk();
            if (params == null) break;

            if (!ChunkAuthenticator.checkIfChunkIsSane(params.validityHashes, params.node,seederRoot)) {
                System.out.println("Invalid Chunk " + params.chunkId + "Chunk Offset " + currChunk);
            } else{
                System.out.println("Valid Chunk " + params.chunkId);
            }
            executor.execute(new ChunkWriter(params.chunkId*s_chunkSize, w, params.chunk));
            currChunk += s_chunkSize;
        }

        executor.shutdown();
    }
}
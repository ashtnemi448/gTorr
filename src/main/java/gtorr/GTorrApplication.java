package gtorr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class GTorrApplication {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        SpringApplication.run(GTorrApplication.class, args);

        String filePath = "r.mp4";
        MerkleTree tree = new MerkleTree(filePath, 1000000);
        MerkleNode root = tree.getRoot();

        ArrayList<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < tree.getLeaves().size(); i++) {
            numbers.add(i);
        }
        Collections.shuffle(numbers);

        ExecutorService executor = Executors.newFixedThreadPool(12);
        ChunkAuthenticator chunkAuthenticator = new ChunkAuthenticator(root);
        RandomAccessFile w = new RandomAccessFile(new File("w.mp4"), "rw");

        for (int i : numbers) {
            RequestParam requestPayload = new RequestParam();
            requestPayload.setFilename(filePath);
            requestPayload.setChunkId(i);
            ResponseParam responsePayload = Downloader.sendPostRequestAndWaitForResponse(requestPayload);

            if (responsePayload == null) {
                System.out.println("EMPTY for chunk " + requestPayload.getChunkId());
                continue;
            }


            int currChunk = 0;
            if (!chunkAuthenticator.checkIfChunkIsSane(responsePayload.getValidityHashList(), new MerkleNode(responsePayload.getHash()))) {
                System.out.println("Invalid Chunk " + requestPayload.getChunkId() + "Chunk Offset " + currChunk);
            } else {
                System.out.println("Valid Chunk " + requestPayload.getChunkId());
            }
            executor.execute(new ChunkWriter(requestPayload.getChunkId() * 1000000, w, responsePayload.getChunk()));
            currChunk += 1000000;
        }

        executor.shutdown();

    }
}


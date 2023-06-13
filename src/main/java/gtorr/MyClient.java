package gtorr;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import static gtorr.ChunkWriter.seederFileLength;

public class MyClient {

    public void sendPostRequest() throws IOException, NoSuchAlgorithmException {
        RestTemplate restTemplate = new RestTemplate();

        String url = "http://localhost:8080/send";

        // Create the request payload
//        ArrayList<String> paramArray = new ArrayList<>();
//        paramArray.add("1");
//        paramArray.add("2");
//        paramArray.add("3");
//
//        RequestParams requestPayload = new RequestParams();
//        requestPayload.setValidityHashList(paramArray);
//        requestPayload.setBytesHash("123");
//        requestPayload.setChunkId(1);
//
//        // Set the request headers
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        // Set the request entity (payload + headers)
//        HttpEntity<RequestParams> requestEntity = new HttpEntity<>(requestPayload, headers);
//
//        // Send the POST request
//        ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Void.class);
//
//        if (response.getStatusCode().is2xxSuccessful()) {
//            // Request was successful
//        } else {
//            // Handle error
//        }

        RandomAccessFile w = new RandomAccessFile(new File("w.mp4"), "rw");
//        RandomAccessFile w = new RandomAccessFile(new File("write.txt"), "rw");
        w.setLength(ChunkWriter.seederFileLength);

        ChunkWriter.seeder();
        ChunkAuthenticator chunkAuthenticator = new ChunkAuthenticator(ChunkWriter.seederRoot);
        int currChunk = 0;

        while (true) {
            ChunkWriter.InputParams params = ChunkWriter.getRandomChunk();
            if (params == null) break;

            if (!chunkAuthenticator.checkIfChunkIsSane(params.validityHashes, params.node)) {
                System.out.println("Invalid Chunk " + params.chunkId + "Chunk Offset " + currChunk);
            } else{
                System.out.println("Valid Chunk " + params.chunkId);
            }

            ArrayList<String> paramArray = new ArrayList<>();

           for( MerkleNode node :params.validityHashes ){
                paramArray.add(node.getHash());
            }

            RequestParams requestPayload = new RequestParams();

            requestPayload.setValidityHashList(paramArray);
            requestPayload.setBytesHash(params.node.getHash());
            requestPayload.setChunk(params.chunk);
            requestPayload.setChunkId(params.chunkId);
            currChunk += ChunkWriter.s_chunkSize;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Set the request entity (payload + headers)
            HttpEntity<RequestParams> requestEntity = new HttpEntity<>(requestPayload, headers);

            // Send the POST request
            ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Void.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                // Request was successful
            } else {
                // Handle error
            }


        }



    }
}

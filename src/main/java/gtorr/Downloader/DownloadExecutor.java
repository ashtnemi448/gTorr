package gtorr.Downloader;

import gtorr.Seeder.ResponseParam;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.util.Pair;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;


public class DownloadExecutor implements Runnable {

    private RequestParam mRequestParam;
    List<RequestParam> mFailedDownload;

    public DownloadExecutor(RequestParam requestParam, List<RequestParam> failedDownload) {
        this.mRequestParam = requestParam;
        this.mFailedDownload = failedDownload;
    }

    @Override
    public void run() {

        RestTemplate restTemplate = new RestTemplate();
        String url = "http://" + mRequestParam.getHost() + "/endpoint";

        // Set the request headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<RequestParam> requestEntity = new HttpEntity<>(mRequestParam, headers);
        ResponseEntity<ResponseParam> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<ResponseParam>() {
                }
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            ResponseParam responsePayload = response.getBody();
            ChunkWriterV2 chunkWriterV2h;
            try {
                chunkWriterV2h = new ChunkWriterV2(responsePayload, mRequestParam , mFailedDownload);
                chunkWriterV2h.writeChunk();
            } catch (Exception e) {
                try {
                    throw new Exception(e);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        } else {
            System.out.println("Error in getting chunkId " + mRequestParam.getChunkId() + "error " + response.getStatusCode());
        }
    }
}

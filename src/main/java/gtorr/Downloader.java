package gtorr;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;

public class Downloader {

    public static ResponseParam sendPostRequestAndWaitForResponse(RequestParam requestPayload) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8080/endpoint";

        // Set the request headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<RequestParam> requestEntity = new HttpEntity<>(requestPayload, headers);
        ResponseEntity<ResponseParam> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<ResponseParam>() {}
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            System.out.println("Error in getting chunkId " + requestPayload.getChunkId() + "error " + response.getStatusCode());
            return null;
        }
    }
}

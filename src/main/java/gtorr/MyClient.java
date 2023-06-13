package gtorr;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

public class MyClient {

    public void sendPostRequest() {
        RestTemplate restTemplate = new RestTemplate();

        String url = "http://localhost:8080/send";

        // Create the request payload
        ArrayList<String> paramArray = new ArrayList<>();
        paramArray.add("1");
        paramArray.add("2");
        paramArray.add("3");

        RequestParams requestPayload = new RequestParams();
        requestPayload.setValidityHashList(paramArray);
        requestPayload.setBytesHash("123");
        requestPayload.setChunkId(1);

        // Set the request headers
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

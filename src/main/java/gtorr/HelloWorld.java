package gtorr;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@RestController
public class HelloWorld {
    @GetMapping("/hello")
    byte[] HelloWorld() throws IOException, NoSuchAlgorithmException {
        String x = "Hello world";
        byte[] b = x.getBytes();
        MyClient client = new MyClient();
        client.sendPostRequest();
        return b;
    }
}


package gtorr;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorld {
    @GetMapping("/hello")
    byte[] HelloWorld(){
        String x = "Hello world";
        byte[] b = x.getBytes();
        MyClient client = new MyClient();
        client.sendPostRequest();
        return b;
    }
}


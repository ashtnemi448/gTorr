package gtorr;

import com.fasterxml.jackson.core.util.RequestPayload;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;



@RestController
public class MyController {

    @PostMapping("/send")
    public void handlePostRequest(@RequestBody RequestParams requestPayload) {
       System.out.println(requestPayload);

        // Do something with the parameters
    }
}


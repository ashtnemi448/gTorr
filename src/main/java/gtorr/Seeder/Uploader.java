package gtorr.Seeder;

import gtorr.Downloader.RequestParam;
import gtorr.Tracker.TrackerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@RestController
public class Uploader {

    @Autowired
    TrackerService trackerService;

    @PostMapping("/endpoint")
    public  ResponseParam upload(@RequestBody RequestParam requestPayload) throws IOException, NoSuchAlgorithmException {
        Seeder seeder = new Seeder();
        return seeder.prepareResponse(requestPayload);
    }
}

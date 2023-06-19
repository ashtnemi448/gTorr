package gtorr;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import static gtorr.ChunkWriter.s_chunkSize;

@RestController
public class Uploader {

    @PostMapping("/endpoint")
    public  ResponseParam upload(@RequestBody RequestParam requestPayload) throws IOException, NoSuchAlgorithmException {
        Seeder seeder = new Seeder();
        return seeder.prepareResponse(requestPayload);
    }
}

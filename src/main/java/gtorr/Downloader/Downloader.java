package gtorr.Downloader;

import gtorr.GTorrApplication;
import gtorr.Seeder.Seeder;
import gtorr.Tracker.TrackerService;
import gtorr.Util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RestController
public class Downloader {

    TrackerService mTrackerService;

    @Autowired
    Downloader(TrackerService trackerService) {
        this.mTrackerService = trackerService;
    }

    @RequestMapping(value = "download/{file}/{fileCkSum}")

    public void download(@PathVariable("fileCkSum") String fileHash, @PathVariable("file") String fileName) throws IOException, NoSuchAlgorithmException, InterruptedException {

        HashSet<String> hosts = mTrackerService.getHosts(fileHash);
        int totalChunks = Math.toIntExact(mTrackerService.getFileSize(fileHash));
        startDownload(fileName, fileHash, totalChunks, hosts);
    }

    private void startDownload(String fileName, String fileHash, int totalChunks, HashSet<String> hosts) throws IOException, NoSuchAlgorithmException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(12);

        for (int i = 0; i < totalChunks; i++) {

            RequestParam requestParam = new RequestParam();
            requestParam.setChunkId(i);
            requestParam.setFileHash(fileHash);
            requestParam.setFileName(fileName);

            String host = Utils.getRandomElementFromHashSet(hosts);
//            System.out.println("Downloading chunk " + i + " from Host - " + host);
            requestParam.setHost(host);

            executor.execute(new DownloadExecutor(requestParam));
        }
        executor.shutdown();
        // Wait for all tasks to complete
        while (!executor.isTerminated()) {}

        // Seed the newly downloaded file
        Seeder.addSeeder(mTrackerService,"Torrent-"+fileName);
    }
}

package gtorr.Downloader;

import gtorr.GTorrApplication;
import gtorr.Seeder.Seeder;
import gtorr.Tracker.TrackerService;
import gtorr.Util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.*;
;
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
        System.out.println(hosts);

        startDownload(fileName, fileHash, totalChunks, hosts);
    }

    private void startDownload(String fileName, String fileHash, int totalChunks, HashSet<String> hosts) throws IOException, NoSuchAlgorithmException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(12);
        List<RequestParam> failedDownload = new ArrayList<>();
        for (int i = 0; i < totalChunks; i++) {

            RequestParam requestParam = new RequestParam();
            requestParam.setChunkId(i);
            requestParam.setFileHash(fileHash);
            requestParam.setFileName(fileName);

            String host = Utils.getRandomElementFromHashSet(hosts);
            requestParam.setHost(host);
            System.out.println("Origi "+ host);
            executor.execute(new DownloadExecutor(requestParam,failedDownload));
        }
        executor.shutdown();
        // Wait for all tasks to complete
        while (!executor.isTerminated()) {}

        ExecutorService retryExecutor = Executors.newFixedThreadPool(12);
        HashMap<String, Integer> hostRetriesMap = new HashMap<>();
        System.out.println("Invalid Chunks " + failedDownload);

        while(!failedDownload.isEmpty() && !hosts.isEmpty()){

            RequestParam req = failedDownload.get(0);
            if(hosts.size() == 1 && req.getHost().equals(Utils.getRandomElementFromHashSet(hosts)))break;
            failedDownload.remove(req);
            System.out.println("Invalid Chunks new " + failedDownload);


            if(hostRetriesMap.containsKey(req.getHost())
                    && hostRetriesMap.get(req.getHost()) >= GTorrApplication.s_maxRetry){
                System.out.println("Removing Host "+ req.getHost());
                hosts.remove(req.getHost());
                hostRetriesMap.remove(req.getHost());
                Runnable threadCode = () -> {
                    mTrackerService.removeHost(req.getFileHash(), req.getHost());
                };
                Thread thread = new Thread(threadCode);
                thread.start();
            }


            if(hosts.contains(req.getHost())) {
                int retryCount = 1;
                if (hostRetriesMap.containsKey(req.getHost())) {
                    retryCount = hostRetriesMap.get(req.getHost()) + 1;
                }
                hostRetriesMap.put(req.getHost(), retryCount);
                System.out.println("rety" + hostRetriesMap);

            }
            String newHost = Utils.getRandomElementFromHashSet(hosts);
            req.setHost(newHost);
            System.out.println("Trying to download "+ req.getChunkId() + "from "+newHost);
            retryExecutor.execute(new DownloadExecutor(req,failedDownload));
        }
        retryExecutor.shutdown();
        // Wait for all tasks to complete
        while (!retryExecutor.isTerminated()) {}

        if(failedDownload.isEmpty()) {
            // Seed the newly downloaded file
            Seeder.addSeeder(mTrackerService, "Torrent-" + fileName);
        } else {
            System.out.println("Download failed");
            File fileToDelete = new File("Torrent-" + fileName);
            if (fileToDelete.exists()) {
                boolean deleted = fileToDelete.delete();
            }
        }
    }
}

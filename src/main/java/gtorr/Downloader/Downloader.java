package gtorr.Downloader;

import gtorr.Tracker.TrackerService;
import gtorr.Util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class Downloader {

    TrackerService mTrackerService;

    @Autowired
    Downloader(TrackerService trackerService) {
        this.mTrackerService = trackerService;
    }

    @RequestMapping(value = "/{file}/{fileCkSum}")
    public void download(@PathVariable("fileCkSum") Long fileCksum, @PathVariable("fileName") String fileName) {
        HashSet<String> hosts = mTrackerService.getHosts(fileCksum);
        Long fileSize = mTrackerService.getFileSize(fileCksum);
        int totalChunks = (int) Math.ceil((double) fileSize / 1000000);
        startDownload(fileName, fileCksum, totalChunks, hosts);
    }

    private void startDownload(String fileName, Long fileCksum, int totalChunks, HashSet<String> hosts) {
        ExecutorService executor = Executors.newFixedThreadPool(12);
        for (int i = 0; i < totalChunks; i++) {

            RequestParam requestParam = new RequestParam();
            requestParam.setChunkId(i);
            requestParam.setFileCkSum(fileCksum);
            requestParam.setFileName(fileName);
            requestParam.setHost(Utils.getRandomElementFromHashSet(hosts));

            executor.execute(new DownloadExecutor(requestParam));
        }
    }
}

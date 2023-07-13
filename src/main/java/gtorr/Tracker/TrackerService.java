package gtorr.Tracker;

import gtorr.GTorrApplication;
import gtorr.Util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class TrackerService {
    private TrackerRepository mTrackerRepository;

    @Autowired
    public TrackerService(TrackerRepository trackerRepository) {
        this.mTrackerRepository = trackerRepository;
    }

    public HashSet<String> getHosts(String fileCkSum) {
        return mTrackerRepository.findById(fileCkSum).get().getHosts();
    }

    public Long getFileSize(String fileCkSum) {
        Tracker tracker = mTrackerRepository.findById(fileCkSum).get();

        return mTrackerRepository.findById(fileCkSum).get().getFileSize();
    }

    public void addSeeder(String file, String fileHash, String host) throws IOException {
        Tracker tracker = mTrackerRepository.findById(fileHash).orElse(null);

        if (tracker != null) {
            tracker.getHosts().add(host);
            tracker.getFileNames().add(file);
            mTrackerRepository.save(tracker);
        } else {
            Tracker newTracker = new Tracker();
            newTracker.setFileHash(fileHash);

            Long numOfChunks = Utils.getNumberOfChunks(file);

            newTracker.setFileSize(numOfChunks);
            newTracker.getFileNames().add(file);

            newTracker.getHosts().add(host);
            mTrackerRepository.save(newTracker);
        }
    }
}

package gtorr.Tracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return mTrackerRepository.findById(fileCkSum).get().getFileSize();
    }

    public void addSeeder(String file, String fileHash, String host) {
        Tracker tracker = mTrackerRepository.findById(fileHash).orElse(null);
        System.out.println(fileHash + " " + file);
        if (tracker != null) {
            tracker.getHosts().add(host);
            tracker.getFileNames().add(file);
            mTrackerRepository.save(tracker);
        } else {
            Tracker newTracker = new Tracker();
            newTracker.setFileHash(fileHash);
            newTracker.getFileNames().add(file);

            newTracker.getHosts().add(host);
            mTrackerRepository.save(newTracker);
        }
    }
}

package gtorr.Tracker;

import gtorr.Util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;

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

    public void removeHost(String fileHash, String host) {
        Tracker tracker = mTrackerRepository.findById(fileHash).orElse(null);
        if (tracker != null) {
            tracker.getHosts().remove(host);
            mTrackerRepository.save(tracker);
        }
    }

    public void addSeeder(String file, String fileHash, String host) throws IOException {

        File f = new File(file);
        String fileName = f.getName();
        Tracker tracker = mTrackerRepository.findById(fileHash).orElse(null);

        if (tracker != null) {
            tracker.getHosts().add(host);
            tracker.getFileNames().add(fileName);
            mTrackerRepository.save(tracker);
        } else {
            Tracker newTracker = new Tracker();
            newTracker.setFileHash(fileHash);

            Long numOfChunks = Utils.getNumberOfChunks(file);

            newTracker.setFileSize(numOfChunks);
            newTracker.getFileNames().add(fileName);

            newTracker.getHosts().add(host);
            mTrackerRepository.save(newTracker);
        }
    }

    public List<Tracker> findAllTrackers() {
        return mTrackerRepository.findAll();
    }
}

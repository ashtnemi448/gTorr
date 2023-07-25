package gtorr;

import gtorr.Downloader.*;
import gtorr.Seeder.ResponseParam;
import gtorr.Seeder.Seeder;
import gtorr.Tracker.Host;
import gtorr.Tracker.HostService;
import gtorr.Tracker.Tracker;
import gtorr.Tracker.TrackerService;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class GTorrApplication {
    static private  PersonsDoa personsDoa;
    static private TrackerService trackerService;
    static private HostService hostService;
    static Environment mEnvironment;

    static public String s_port = "9091";
    static public int s_chunkSize = 1000000;
    static public int s_maxRetry = 2;

    @Autowired
    public GTorrApplication(PersonsDoa personsDoa, TrackerService trackerService, HostService hostService) {
        this.personsDoa = personsDoa;
        this.trackerService = trackerService;
        this.hostService = hostService;
    }

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        SpringApplication.run(GTorrApplication.class, args);
        Seeder seeder = new Seeder();
        seeder.initSeeder(hostService,trackerService);
        System.out.println("System Boot Complete");
    }
}


package gtorr;

import gtorr.Seeder.Seeder;
import gtorr.Tracker.TrackerService;
import org.apache.commons.cli.*;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@SpringBootApplication
public class GTorrApplication {
    static private TrackerService trackerService;

    static public String s_port = "9092";
    static public int s_chunkSize = 5000000;
    static public int s_maxRetry = 2;
    static public String wd = "";

    @Autowired
    public GTorrApplication(TrackerService trackerService) {
        GTorrApplication.trackerService = trackerService;
    }

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        Options options = new Options();
        options.addOption("w", "workspace", true, "Specify directory to run torrent from");
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            String workingDir = cmd.getOptionValue("workspace");
            if (workingDir.isEmpty()) {
                wd = ".";
            } else {
                wd = workingDir;
            }
        } catch (ParseException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
        System.out.println("Workspace "+ wd);
        SpringApplication.run(GTorrApplication.class, args);
        Seeder seeder = new Seeder();
        seeder.initSeeder(trackerService);
        System.out.println("System Boot Complete");
    }
}


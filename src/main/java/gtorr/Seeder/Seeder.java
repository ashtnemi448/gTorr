package gtorr.Seeder;

import gtorr.Downloader.PersistentMerkelTree;
import gtorr.Downloader.RequestParam;
//import gtorr.Downloader.MerkleTree;
import gtorr.GTorrApplication;
import gtorr.Tracker.TrackerService;
import gtorr.Util.HashUtils;
import gtorr.Util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.NoSuchAlgorithmException;

import java.util.*;

@RestController
@Component
public class Seeder {

    static int initFlag = 0;

    @Autowired
    public void initSeeder(TrackerService trackerService) throws IOException, NoSuchAlgorithmException {
        if (initFlag == 1) return;
        List<String> filesToSeed = getFilesToSeed();
        System.out.println(filesToSeed);
        for (String file : filesToSeed) {
            File f = new File(GTorrApplication.wd + "/" + file + ".cache");
            if (file.contains(".cache") || f.exists()) {
                if (file.contains(".cache")) {
                    System.out.println(" File - " + file + " hash - " + PersistentMerkelTree.getHashForFile(GTorrApplication.wd + "/" +file));
                    continue;
                }
                continue;
            }
            String root = PersistentMerkelTree.cacheMetadata(GTorrApplication.wd + "/" + file);

            System.out.println("File - " + file + " hash - " + root);
            trackerService.addSeeder(GTorrApplication.wd + "/" +file, root, getPrivateIp() + ":" + GTorrApplication.s_port);
        }
        initFlag = 1;
    }

    @Autowired
    public static void addSeeder(TrackerService trackerService, String file, String rootHash) throws IOException, NoSuchAlgorithmException {
        trackerService.addSeeder(file, rootHash, getPrivateIp() + ":" + GTorrApplication.s_port);
    }

    private static String getPrivateIp() {
        String privateIp = "";
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (!address.isLoopbackAddress() && !address.isLinkLocalAddress() && address.isSiteLocalAddress()) {
                        privateIp = address.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return privateIp;
    }

    public static List<String> getFilesToSeed() {
        List<String> filesToUpload = new ArrayList<>();
        String dirPath = GTorrApplication.wd;

        File path = new File(dirPath);
        File[] files = path.listFiles();
        for (int i = 0; i < Objects.requireNonNull(files).length; i++) {

            if (files[i].isFile() && !files[i].isDirectory()) {
                filesToUpload.add(files[i].getName());
            }
        }
        return filesToUpload;
    }

    public ResponseParam prepareResponse(RequestParam requestParam) throws IOException, NoSuchAlgorithmException {
        ResponseParam responseParam = new ResponseParam();

        byte[] chunk = Utils.getChunk(requestParam.getChunkId() * (GTorrApplication.s_chunkSize), GTorrApplication.wd + "/" +requestParam.getFileName());
        responseParam.setChunk(chunk);
        String hashStr = HashUtils.bytesToHex(chunk);
        responseParam.setHash(hashStr);
        responseParam.setValidityHashList(PersistentMerkelTree.getValidityHash(GTorrApplication.wd + "/" +requestParam.getFileName(), requestParam.getChunkId()));
        responseParam.setRootHash(PersistentMerkelTree.getHashForFile(GTorrApplication.wd + "/" +requestParam.getFileName()));
        return responseParam;
    }
}

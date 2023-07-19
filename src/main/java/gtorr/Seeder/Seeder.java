package gtorr.Seeder;

import gtorr.Downloader.RequestParam;
import gtorr.Downloader.MerkleNode;
import gtorr.Downloader.MerkleTree;
import gtorr.GTorrApplication;
import gtorr.Tracker.HostService;
import gtorr.Tracker.TrackerService;
import gtorr.Util.HashUtils;
import gtorr.Util.Utils;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.boot.autoconfigure.web.ServerProperties;

import java.util.*;

@RestController
@Component
public class Seeder {

    private static final HashMap<String, MerkleTree> sFileTreeMap = new HashMap<>();

    public static synchronized void cacheFile(String file) throws IOException, NoSuchAlgorithmException {
        if (!sFileTreeMap.containsKey(file)) {
            sFileTreeMap.put(file, new MerkleTree(file, GTorrApplication.s_chunkSize));
        } else {
//            System.out.println("Contains new file");
        }
    }


    @Autowired
    public void initSeeder(HostService hostService, TrackerService trackerService) throws IOException, NoSuchAlgorithmException {
        List<String> filesToSeed = getFilesToSeed();
        System.out.println(filesToSeed);
        for (String file : filesToSeed) {
            Seeder.cacheFile(file);
            System.out.println(" init file - " + file + " hash - " + sFileTreeMap.get(file).getRoot().getHash());
            trackerService.addSeeder(file, sFileTreeMap.get(file).getRoot().getHash(), getPrivateIp() + ":" + GTorrApplication.s_port);
        }
    }

    @Autowired
    public static void addSeeder(TrackerService trackerService, String file) throws IOException, NoSuchAlgorithmException {
        Seeder.cacheFile(file);
//        System.out.println(" Seeding new file - " + file + " hash - " + sFileTreeMap.get(file).getRoot().getHash());
        trackerService.addSeeder(file, sFileTreeMap.get(file).getRoot().getHash(), getPrivateIp() + ":");
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
        String dirPath = System.getProperty("user.dir");

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
        MerkleTree tree = sFileTreeMap.get(requestParam.getFileName());

        responseParam.setChunk(Utils.getChunk(requestParam.getChunkId() * (GTorrApplication.s_chunkSize), requestParam.getFileName()));
        String hashStr = HashUtils.bytesToHex(Utils.getChunk(requestParam.getChunkId(),requestParam.getFileName()));
        responseParam.setHash(hashStr);
        responseParam.setValidityHashList(tree.getValidityHash(hashStr));
        responseParam.setRootHash(tree.getRoot().getHash());
        return responseParam;
    }


}

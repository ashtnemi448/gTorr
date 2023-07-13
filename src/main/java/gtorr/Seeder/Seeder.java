package gtorr.Seeder;

import gtorr.Downloader.RequestParam;
import gtorr.Downloader.MerkleNode;
import gtorr.Downloader.MerkleTree;
import gtorr.GTorrApplication;
import gtorr.Tracker.HostService;
import gtorr.Tracker.TrackerService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
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

    private static final HashMap<String, MerkleTree> sFileTreeMap = new HashMap<>();

    @Autowired
    public  void initSeeder(HostService hostService, TrackerService trackerService) throws IOException, NoSuchAlgorithmException {
        List<String> filesToSeed = getFilesToSeed();
        System.out.println(filesToSeed);
        String serverPort = "7777";
        for (String file : filesToSeed) {

            if (!sFileTreeMap.containsKey(file)) {
                sFileTreeMap.put(file, new MerkleTree(file, GTorrApplication.s_chunkSize));
            }
            System.out.println(" file - "+ file + " hash - " + sFileTreeMap.get(file).getRoot().getHash());
            trackerService.addSeeder(file, sFileTreeMap.get(file).getRoot().getHash(), getPrivateIp() + ":" + serverPort);
        }
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

        MerkleNode node = tree.getLeaves().get(requestParam.getChunkId());

        responseParam.setChunk(node.getChunkBytes());
        responseParam.setHash(node.getHash());
        responseParam.setValidityHashList(tree.getValidityHash(node));
        responseParam.setRootHash(tree.getRoot().getHash());
        return responseParam;
    }
}
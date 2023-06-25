package gtorr.Seeder;

import gtorr.Downloader.RequestParam;
import gtorr.Downloader.MerkleNode;
import gtorr.Downloader.MerkleTree;
import gtorr.Tracker.HostService;
import gtorr.Tracker.TrackerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.zip.CRC32;

@RestController
@Component
public class Seeder {

    @Autowired
    static
    Environment environment;
    private static final HashMap<String, MerkleTree> sFileTreeMap = new HashMap<>();

    @Autowired
    public static void initSeeder(HostService hostService, TrackerService trackerService) throws IOException, NoSuchAlgorithmException {
        List<String> filesToSeed = getFilesToSeed();

        for (String file : filesToSeed) {
            System.out.println("port" + environment.getProperty("local.server.port"));
            if (!sFileTreeMap.containsKey(file)) {
                sFileTreeMap.put(file, new MerkleTree(file, 1000000));
            }
            trackerService.addSeeder(file, sFileTreeMap.get(file).getRoot().getHash(), getPrivateIp() + ":" + environment.getProperty("local.server.port"));
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
        System.out.println(dirPath);
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

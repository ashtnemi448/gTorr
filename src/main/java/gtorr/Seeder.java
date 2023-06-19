package gtorr;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static gtorr.ChunkWriter.s_chunkSize;

@RestController
public class Seeder {

    private static final HashMap<String,MerkleTree> sFileTreeMap = new HashMap<>();

    public ResponseParam prepareResponse(RequestParam requestParam) throws IOException, NoSuchAlgorithmException {
        ResponseParam responseParam = new ResponseParam();
        MerkleTree tree = null;

        if(sFileTreeMap.containsKey(requestParam.getFilename())){
            tree = sFileTreeMap.get(requestParam.getFilename());
        } else {
            tree = new MerkleTree(requestParam.getFilename(), 1000000);
        }
        MerkleNode node = tree.getLeaves().get(requestParam.getChunkId());

        responseParam.setChunk(node.getChunkBytes());
        responseParam.setHash(node.getHash());
        responseParam.setValidityHashList(tree.getValidityHash(node));
        return responseParam;
    }
}

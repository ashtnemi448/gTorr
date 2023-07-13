package gtorr;

import gtorr.Downloader.MerkleTree;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

@SpringBootTest
public class MerkleTreeTestsV1 {
    @Test
    public void test_hash() {
        String hash = "hash";
        byte[] bytesHash = hash.getBytes();
        Assert.assertEquals("d04b98f48e8f8bcc15c6ae5ac050801cd6dcfd428fb5f9e65c4e16e7807340fa", MerkleTree.hash(bytesHash));
    }

    @Test
    public void test_merkleTreeSmallInput() throws IOException {


        MerkleTree merkleTree = new MerkleTree("src/test/java/gtorr/TestFiles/SmallInput.txt", 1);
        merkleTree.setChunkSize(10);

        Assert.assertEquals(merkleTree.getRoot(), "62af5c3cb8da3e4f25061e829ebeea5c7513c54949115b1acc225930a90154da");

        List<String> leaves = new ArrayList<>();
        leaves.add("ca978112ca1bbdcafac231b39a23dc4da786eff8147c4e72b9807785afee48bb");
        leaves.add("3e23e8160039594a33894f6564e1b1348bbd7a0088d42c4acb73eeaed59c009d");

        Assert.assertEquals(leaves, merkleTree.getLeaves());
    }

    @Test
    public void test_merkleTreeSmallInputBigChunk() throws IOException {

        MerkleTree merkleTree = new MerkleTree("src/test/java/gtorr/TestFiles/SmallInput.txt", 2);
        Assert.assertEquals(merkleTree.getRoot(), "fb8e20fc2e4c3f248c60c39bd652f3c1347298bb977b8b4d5903b85055620603");

        List<String> leaves = new ArrayList<>();
        leaves.add("fb8e20fc2e4c3f248c60c39bd652f3c1347298bb977b8b4d5903b85055620603");

        Assert.assertEquals(leaves, merkleTree.getLeaves());
    }

    @Test
    public void test_merkleTreeBigInput() throws IOException {
        MerkleTree merkleTree = new MerkleTree("src/test/java/gtorr/TestFiles/BigInput.txt", 1);
        Assert.assertEquals(merkleTree.getRoot(), "0bdf27bf7ec894ca7cadfe491ec1a3ece840f117989e8c5e9bd7086467bf6c38");

        List<String> leaves = new ArrayList<>();
        leaves.add("ca978112ca1bbdcafac231b39a23dc4da786eff8147c4e72b9807785afee48bb");
        leaves.add("3e23e8160039594a33894f6564e1b1348bbd7a0088d42c4acb73eeaed59c009d");
        leaves.add("2e7d2c03a9507ae265ecf5b5356885a53393a2029d241394997265a1a25aefc6");

        Assert.assertEquals(leaves, merkleTree.getLeaves());
    }
    @Test
    public void test_merkleTreeBigInputBigChunk() throws IOException {
        MerkleTree merkleTree = new MerkleTree("src/test/java/gtorr/TestFiles/BigInput.txt", 3);
        Assert.assertEquals(merkleTree.getRoot(), "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad");

        List<String> leaves = new ArrayList<>();
        leaves.add("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad");

        Assert.assertEquals(leaves, merkleTree.getLeaves());
    }
}

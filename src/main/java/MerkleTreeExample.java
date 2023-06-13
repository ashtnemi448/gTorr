import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class MerkleTree {
    private List<String> leaves;
    private List<String> tree;

    public MerkleTree(List<String> leaves) {
        this.leaves = leaves;
        this.tree = buildTree();
    }

    private List<String> buildTree() {
        List<String> tree = new ArrayList<>(leaves);

        int levelSize = leaves.size();
        while (levelSize > 1) {
            List<String> nextLevel = new ArrayList<>();

            for (int i = 0; i < levelSize; i += 2) {
                String leftChild = tree.get(i);
                String rightChild = (i + 1 < levelSize) ? tree.get(i + 1) : leftChild;
                String parent = hash(leftChild + rightChild);
                nextLevel.add(parent);
            }

            tree = nextLevel;
            levelSize = tree.size();
        }

        return tree;
    }

    public String getRoot() {
        return tree.get(0);
    }

    private String hash(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available.", e);
        }
    }
}

public class MerkleTreeExample {
    public static void main(String[] args) {
        String filePath = "data1.txt";
        int chunkSize = 1; // 1MB chunk size

        try {
            byte[] fileBytes = readFileBytes(filePath);
            List<String> leaves = generateLeaves(fileBytes, chunkSize);

            MerkleTree merkleTree = new MerkleTree(leaves);
            String originalRoot = merkleTree.getRoot();
            System.out.println("Original Merkle Tree Root: " + originalRoot);

            // Replace a random byte chunk
            Random random = new Random();
            int randomIndex = random.nextInt(leaves.size());
            leaves.set(randomIndex, generateRandomLeaf(chunkSize));

            MerkleTree modifiedMerkleTree = new MerkleTree(leaves);
            String modifiedRoot = modifiedMerkleTree.getRoot();
            System.out.println("Modified Merkle Tree Root: " + modifiedRoot);

            boolean rootsMatch = originalRoot.equals(modifiedRoot);
            System.out.println("Roots Match: " + rootsMatch);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static byte[] readFileBytes(String filePath) throws IOException {
        FileInputStream inputStream = new FileInputStream(filePath);
        byte[] fileBytes = new byte[inputStream.available()];
        inputStream.read(fileBytes);
        inputStream.close();
        return fileBytes;
    }

    private static List<String> generateLeaves(byte[] fileBytes, int chunkSize) {
        List<String> leaves = new ArrayList<>();

        int startPos = 0;
        int remainingBytes = fileBytes.length;

        while (remainingBytes > 0) {
            int chunkBytes = Math.min(chunkSize, remainingBytes);
            byte[] chunk = new byte[chunkBytes];
            System.arraycopy(fileBytes, startPos, chunk, 0, chunkBytes);
            String leaf = hash(chunk);
            leaves.add(leaf);

            startPos += chunkSize;
            remainingBytes -= chunkSize;
        }

        return leaves;
    }

    private static String generateRandomLeaf(int chunkSize) {
        byte[] randomBytes = new byte[chunkSize];
        new Random().nextBytes(randomBytes);
        return hash(randomBytes);
    }

    private static String hash(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available.", e);
        }
    }
}

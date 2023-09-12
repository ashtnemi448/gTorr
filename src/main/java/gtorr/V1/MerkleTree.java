//package gtorr.Downloader;
//
//import gtorr.GTorrApplication;
//import gtorr.Util.HashUtils;
//import gtorr.Util.Utils;
//
//import java.io.*;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//
//
//
//public class MerkleTree {
//    private MerkleNode mRoot;
//    private int mChunkSize;
//    List<MerkleNode> mLeafNodes;
//    private List<MerkleNode>  mLastLeaf ;
//    private HashMap<String,MerkleNode> mHashNodeMap = new HashMap<>();
//
//    public MerkleTree(String filePath, int chunkSize) throws IOException, NoSuchAlgorithmException {
//        this.mLastLeaf = new ArrayList<>();
//        this.mChunkSize = chunkSize;
//        this.mRoot = buildTreeFromFile(filePath);
//    }
//
//    public List<MerkleNode> getLeaves() {
//        return mLeafNodes;
//    }
//
//    public MerkleNode getRoot() {
//        return mRoot;
//    }
//
//    private MerkleNode getBrother(MerkleNode node) {
//        if (node == null) return null;
//
//        MerkleNode parent = node.getParent();
//
//        if (parent == null) return null;
//
//        MerkleNode left = parent.getLeft();
//        MerkleNode right = parent.getRight();
//
//        if (left.getHash().equals(node.getHash())) {
//            return right;
//        }
//
//        return left;
//    }
//
//    private MerkleNode getUncle(MerkleNode node) {
//        MerkleNode parent = node.getParent();
//        return getBrother(parent);
//    }
//
//    private MerkleNode getParent(MerkleNode current/*, MerkleNode node*/) {
//        if (current == null) return null;
//        return current.getParent();
//    }
//
//    private MerkleNode buildTreeFromFile(String filePath) throws IOException, NoSuchAlgorithmException {
////        List<byte[]> fileChunks = divideFileIntoChunks(filePath);
//        mLeafNodes = createLeafNodes(filePath);
//
//        List<MerkleNode> leaves = mLeafNodes;
//
//        while (leaves.size() > 1) {
//            List<MerkleNode> parents = new ArrayList<>();
//            for (int i = 0; i < leaves.size(); i += 2) {
//                MerkleNode parent;
//                MerkleNode left = leaves.get(i);
//                MerkleNode right = (i + 1 < leaves.size()) ? leaves.get(i + 1) : null;
//
//                if (right == null){
//                    mLastLeaf.add(left);
//                    continue;
//                }
//                else
//                    parent = createParentNode(left, right);
//                parents.add(parent);
//            }
//            leaves = null;
//            leaves = parents;
//        }
//
//        if (leaves.size() == 1)
//            mRoot = leaves.get(0);
//
//        for(int i = mLastLeaf.size()-1;i>=0;i--){
//            mRoot = createParentNode(mRoot,mLastLeaf.get(i));
//        }
//        return mRoot;
//    }
//
//    private MerkleNode createParentNode(MerkleNode left, MerkleNode right) throws NoSuchAlgorithmException {
//        String combinedHash = HashUtils.concatenateHashes(left.getHash(), right.getHash());
//        MerkleNode parent = new MerkleNode(combinedHash);
//
//        parent.setLeft(left);
//        parent.setRight(right);
//
//        left.setIsLeft(1);
//        left.setParent(parent);
//
//        right.setParent(parent);
//        right.setIsLeft(0);
//
//        return parent;
//    }
//
//    private List<byte[]> divideFileIntoChunks(String filePath) throws IOException {
////        Path path = Path.of(filePath);
////        byte[] fileData = Files.readAllBytes(path);
////        int numOfChunks = Math.toIntExact(Utils.getNumberOfChunks(filePath));
//
//        int chunkSize = mChunkSize;
//        List<byte[]> chunks = new ArrayList<>();
//
//        byte[] chunk = new byte[chunkSize];
//        FileInputStream fin = new FileInputStream(filePath);
//        BufferedInputStream bin = new BufferedInputStream(fin);
//
//        while (bin.available() > 0) {
//            if(bin.available() <=  mChunkSize ){
//                chunk = new byte[bin.available()];
//                bin.read(chunk,0, bin.available());
//            } else {
//                bin.read(chunk, 0, mChunkSize);
//            }
//            chunks.add(chunk);
//            chunk = new byte[chunkSize];
//        }
//
//        return chunks;
//    }
//
//    public List<MerkleNode> createLeafNodes(String filePath) throws NoSuchAlgorithmException, IOException {
//        List<MerkleNode> leafNodes = new ArrayList<>();
//
//        int chunkSize = mChunkSize;
//
//        byte[] chunk = new byte[chunkSize];
//        FileInputStream fin = new FileInputStream(filePath);
//        BufferedInputStream bin = new BufferedInputStream(fin);
//
//        while (bin.available() > 0) {
//            if(bin.available() <=  mChunkSize ){
//                chunk = new byte[bin.available()];
//                bin.read(chunk,0, bin.available());
//            } else {
//                bin.read(chunk, 0, mChunkSize);
//            }
//
//            String hashStr = HashUtils.bytesToHex(chunk);
//            MerkleNode node  = new MerkleNode(hashStr);
//            leafNodes.add(node);
//            mHashNodeMap.put(hashStr,node);
//            chunk = new byte[chunkSize];
//        }
//        return leafNodes;
//    }
//
//    public void printNodeDetails(MerkleNode node) {
//        if (node != null) {
//            System.out.println("Node: " + node.getHash());
//
//            MerkleNode brother = getBrother(node);
//            if (brother != null)
//                System.out.println("Brother: " + brother.getHash());
//            else
//                System.out.println("No brother exists for this node.");
//
//            MerkleNode uncle = getUncle(node);
//            if (uncle != null)
//                System.out.println("Uncle: " + uncle.getHash());
//            else
//                System.out.println("No uncle exists for this node.");
//        }
//    }
//
//    void print(MerkleNode node) throws NoSuchAlgorithmException {
//        if (node == null) return;
//        print(node.getLeft());
//
//        List<ValidityHash> validityHash = getValidityHash(node.getHash());
//
//        boolean result = verifyTree(validityHash, node);
//        if (result)
//            System.out.println("SANE ");
//        else
//            System.out.println("INSANE ");
//        print(node.getRight());
//    }
//
//    public List<ValidityHash> getValidityHash(String hash) {
//
//        List<ValidityHash> validityHashes = new ArrayList<>();
//        if(mHashNodeMap.containsKey(hash) == false){
//            return validityHashes;
//        }
//        MerkleNode node = mHashNodeMap.get(hash);
//        if (getBrother(node) != null)
//            validityHashes.add(new ValidityHash(getBrother(node).getHash(),getBrother(node).getIsLeft()));
//
//        while (getParent(node) != null) {
//            MerkleNode uncle = getUncle(node);
//            if (uncle != null)
//                validityHashes.add(new ValidityHash(uncle.getHash(),uncle.getIsLeft()));
//            node = uncle;
//        }
//        return validityHashes;
//    }
//
//    boolean verifyTree(List<ValidityHash> validityHashes, MerkleNode node) throws NoSuchAlgorithmException {
//        MerkleNode currNode = node;
//
//        for (ValidityHash validityHash : validityHashes) {
//            if (validityHash.getHash().equals(mRoot.getHash())) {
//                break;
//            }
//            String parentHash = "";
//            String combinedHash;
//            if (validityHash.getIsLeft() == 0)
//                combinedHash = HashUtils.concatenateHashes(currNode.getHash(), validityHash.getHash());
//            else
//                combinedHash = HashUtils.concatenateHashes(validityHash.getHash(), currNode.getHash());
//            parentHash = combinedHash;
//            currNode = new MerkleNode(parentHash);
//        }
//        return currNode.getHash().equals(mRoot.getHash());
//    }
//
//    /*================================= TESTING ================================ */
//    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
//        MerkleTree merkleTree = new MerkleTree("r.mp4", GTorrApplication.s_chunkSize);
//        List<MerkleNode> leaves = merkleTree.getLeaves();
//        for (int i = 0; i < leaves.size(); i++) {
//            if (!merkleTree.verifyTree(merkleTree.getValidityHash(leaves.get(i).getHash()),leaves.get(i))) {
//                System.out.println("Invalid Chunk " + i);
//            } else {
//                System.out.println("Valid Chunk " + i);
//            }
//        }
//    }
//}
///*
// init file - Design Issues hash - 7ac93d5d9aba137f704e767753ff1fdb22e2fd582124843911b19aa01bbbd96f
// init file - HELP.md hash - 639d766d7c9cac900563ff1b5eb8176093da9af2fed86fe45d23144ae3007181
// init file - gTorr.iml hash - e983ca85315d2d3a6a25f6dd157f6851f0262081d1b60054936eed574fba171f
// init file - write.txt hash - ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad
// init file - mvnw.cmd hash - 6d19e53372cb157e46f62b648832d34ab12f0e6b6b9f5f10b643f1f010bcc4c9
// init file - sample.mp4 hash - d964d257227a57b675c7f07687ded8c27bcf2997381ca335667d13e03d59da5c
// init file - pom.xml hash - 59494e4375d96f719280e07beb94ee9d321508fe48564b88b6bedd0ca81f1d8e
// init file - smallTest.txt hash - 88d4266fd4e6338d13b845fcf289579d209c897823b9217da3e161936f031589
// init file - read.txt hash - 9bdff4be406b010eaa66ab32e8d0e0b9253935c7b486eec8f651cccb50379c0f
// init file - mvnw hash - 4591ada725d05dbb596064ff1540d673a6252036699596d279ce8117606e823f
//
//1000000
//
// */
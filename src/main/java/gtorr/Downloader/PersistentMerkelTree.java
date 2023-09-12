package gtorr.Downloader;

import gtorr.GTorrApplication;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

public class PersistentMerkelTree {
    static final int HASH_SIZE = 40;
    static final int SEPERATOR_SIZE = 1; // ':' 
    static final int ADDRESS_SIZE = 8;
    static final int IS_LEFT_FLAG_SIZE = 1;

    static int chunkSize = GTorrApplication.s_chunkSize;
    static final int recordSizeBytes = HASH_SIZE + SEPERATOR_SIZE + ADDRESS_SIZE + IS_LEFT_FLAG_SIZE;
    static HashMap<String, String> fileRootHashMap = new HashMap<>();

    static public String getHash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(input.getBytes(StandardCharsets.UTF_8));
        byte[] digest = md.digest();

        StringBuilder hexString = new StringBuilder();
        for (byte b : digest) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    static void persistRecord(long writeOffset, String chunkHash, long parentAddress, RandomAccessFile file) throws IOException {
        file.seek(writeOffset);
        file.write(chunkHash.getBytes());
        file.write(new String(":").getBytes());
        file.writeLong(parentAddress);
        file.writeByte(0);
    }

    static String getChunkHash(long chunkOffset, RandomAccessFile file) throws IOException {
        file.seek(chunkOffset);
        byte[] chunk = new byte[HASH_SIZE];
        file.read(chunk);
        return new String(chunk);
    }

    static byte getIsLeft(long chunkOffset, RandomAccessFile file) throws IOException {
        file.seek(chunkOffset + HASH_SIZE + SEPERATOR_SIZE + ADDRESS_SIZE);
        return file.readByte();
    }

    static long getParentOffset(long chunkOffset, RandomAccessFile file) throws IOException {
        file.seek(chunkOffset + HASH_SIZE + SEPERATOR_SIZE);
        return file.readLong();
    }

    static void updateParentAddress(long chunkOffset, long parentAddress, RandomAccessFile file, byte isLeft) throws IOException {
        file.seek(chunkOffset + HASH_SIZE + SEPERATOR_SIZE);
        file.writeLong(parentAddress);
        file.seek(chunkOffset + HASH_SIZE + SEPERATOR_SIZE + ADDRESS_SIZE);
        file.writeByte(isLeft);
    }

    static long getBrother(long chunkOffset, RandomAccessFile file) throws IOException {
        chunkOffset = getParentRecursive(chunkOffset, file);
        file.seek(chunkOffset + HASH_SIZE + SEPERATOR_SIZE + ADDRESS_SIZE);
        byte isLeft = file.readByte();
        if (isLeft == 1) {
            chunkOffset += HASH_SIZE + SEPERATOR_SIZE + ADDRESS_SIZE + IS_LEFT_FLAG_SIZE;
        } else {
            chunkOffset -= HASH_SIZE + SEPERATOR_SIZE + ADDRESS_SIZE + IS_LEFT_FLAG_SIZE;
        }
        if (chunkOffset >= file.length()) return -1;
        return chunkOffset;
    }

    static long getUncle(long chunkOffset, RandomAccessFile file) throws IOException {
        long parent = getParentRecursive(chunkOffset, file);
        if (parent + HASH_SIZE + SEPERATOR_SIZE + ADDRESS_SIZE + IS_LEFT_FLAG_SIZE >= file.length()) return -1;
        parent = getParentOffset(parent, file);

        if (parent + HASH_SIZE + SEPERATOR_SIZE + ADDRESS_SIZE + IS_LEFT_FLAG_SIZE >= file.length()) return parent;

        long brother = getBrother(parent, file);
        if (brother == -1) return -1;

        return brother;
    }

    static long getParentRecursive(long chunkOffset, RandomAccessFile file) throws IOException {
        if (chunkOffset + HASH_SIZE + SEPERATOR_SIZE >= file.length()) return -1;

        long currOffset = chunkOffset;
        long parent = getParentOffset(chunkOffset, file);
        if (parent == 0) return 0;

        String currHash = getChunkHash(chunkOffset, file);
        String parentHash = getChunkHash(parent, file);

        while (currHash.equals(parentHash)) {
            if (parent + HASH_SIZE + SEPERATOR_SIZE + ADDRESS_SIZE + IS_LEFT_FLAG_SIZE >= file.length()) return -1;

            currOffset = parent;
            parent = getParentOffset(currOffset, file);
            if (parent == 0) break;

            parentHash = getChunkHash(parent, file);
        }
        return currOffset;
    }

    static void writeLeafNodesToFile(RandomAccessFile readFile, RandomAccessFile WriteFile) throws IOException, NoSuchAlgorithmException {
        long writeOffset = 0;
        int tmpChunkSize = chunkSize;
        if (tmpChunkSize > readFile.length()) {
            tmpChunkSize = (int) readFile.length();
        }
        byte[] readChunk = new byte[tmpChunkSize];
        int i;

        for (i = 0; i < readFile.length(); ) {
            readFile.seek(i);
            if (i + tmpChunkSize > readFile.length()) {
                readChunk = new byte[(int) (readFile.length() - i)];
                readFile.read(readChunk);
            } else {
                readFile.read(readChunk);
            }
            String readStr = getHash(new String(readChunk));
            persistRecord(writeOffset, readStr, 0, WriteFile);
            writeOffset += recordSizeBytes;
            i += tmpChunkSize;
        }
    }

    static public String getRootLeafHash(String file, int chunkId) throws IOException {
        RandomAccessFile rd = new RandomAccessFile(file + ".cache", "r");
        return getChunkHash(chunkId * (HASH_SIZE + SEPERATOR_SIZE + ADDRESS_SIZE + IS_LEFT_FLAG_SIZE), rd);

    }


    static public ArrayList<ValidityHash> getValidityHash(String fileName, int chunk) throws IOException {
        fileName += ".cache";
        ArrayList<ValidityHash> validityString = new ArrayList<>();
        RandomAccessFile rd = new RandomAccessFile(fileName, "rw");
        byte[] b = new byte[HASH_SIZE + SEPERATOR_SIZE + ADDRESS_SIZE + IS_LEFT_FLAG_SIZE];
        long off = (long) chunk * (HASH_SIZE + SEPERATOR_SIZE + ADDRESS_SIZE + IS_LEFT_FLAG_SIZE);

        off = getParentRecursive(off, rd);
        off = getBrother(off, rd);
        if (off < 0 || off >= rd.length()) {
            return validityString;
        }
        ValidityHash v = new ValidityHash();
        v.mHash = getChunkHash(off, rd);
        v.mIsLeft = (int) getIsLeft(off, rd);

        validityString.add(v);

        while (off + HASH_SIZE + SEPERATOR_SIZE + ADDRESS_SIZE + IS_LEFT_FLAG_SIZE < rd.length()) {
            off = getParentRecursive(off, rd);
            if (off + HASH_SIZE + SEPERATOR_SIZE + ADDRESS_SIZE + IS_LEFT_FLAG_SIZE >= rd.length()) {
                return validityString;
            }

            off = getUncle(off, rd);
            if (off == -1 || off + HASH_SIZE + SEPERATOR_SIZE + ADDRESS_SIZE + IS_LEFT_FLAG_SIZE >= rd.length()) {
                return validityString;
            }
            v = new ValidityHash();
            v.mHash = getChunkHash(off, rd);
            v.mIsLeft = (int) getIsLeft(off, rd);
            validityString.add(v);
        }
        return validityString;
    }

    static public String cacheMetadata(String fileName) throws IOException, NoSuchAlgorithmException {
        long startTime1 = System.currentTimeMillis();
        RandomAccessFile rd = new RandomAccessFile(fileName, "r");
        RandomAccessFile fos = new RandomAccessFile(fileName + ".cache", "rw");
        long e1 = System.currentTimeMillis();

        fos.setLength(0);
        writeLeafNodesToFile(rd, fos);

        long start = 0;
        long end = fos.length();
        long writeOffset = end;
        String rootHash = getChunkHash(0, fos);

        long j;
        while (end - start != recordSizeBytes) {
            for (j = start; j < end; ) {
                long nextj = j + recordSizeBytes;
                if (nextj >= end) {
                    rootHash = getChunkHash(j, fos);
                    persistRecord(writeOffset, rootHash, 0, fos);
                    updateParentAddress(j, writeOffset, fos, (byte) 0);
                    writeOffset += recordSizeBytes;
                    j = j + recordSizeBytes;
                    continue;
                }
                String parent = getHash(getChunkHash(j, fos) + getChunkHash(nextj, fos));
                rootHash = parent;
                persistRecord(writeOffset, parent, 0, fos);
                updateParentAddress(j, writeOffset, fos, (byte) 1);
                updateParentAddress(nextj, writeOffset, fos, (byte) 0);
                j = nextj + recordSizeBytes;
                writeOffset += recordSizeBytes;
            }
            start = j;
            end = writeOffset;
        }
        fileRootHashMap.put(fileName, rootHash);
        long endTime1 = System.currentTimeMillis();
        System.out.println("Elapsed time to seed file " + (endTime1 - startTime1) + " Root - " + getHashForFile(fileName+".cache"));
        return rootHash;
    }

    static public String getHashForFile(String file) throws IOException {
        RandomAccessFile f = new RandomAccessFile(file, "r");
        if (f.length() <= HASH_SIZE + SEPERATOR_SIZE + ADDRESS_SIZE + IS_LEFT_FLAG_SIZE) {
            return getChunkHash(0, f);
        }
        return getChunkHash(f.length() - (HASH_SIZE + SEPERATOR_SIZE + ADDRESS_SIZE + IS_LEFT_FLAG_SIZE), f);
    }

    public static void main(String... args) throws IOException, NoSuchAlgorithmException {

        RandomAccessFile fos = new RandomAccessFile("read.txt.cache", "rw");
        cacheMetadata("read.txt");
        for (int i = 0; i < fos.length(); i += HASH_SIZE + SEPERATOR_SIZE + ADDRESS_SIZE + IS_LEFT_FLAG_SIZE) {
            System.out.println("chunk hash " + i + " " + getChunkHash(i, fos));
        }

        System.out.println("");
        for (int i = 0; i <= 13; i++) {
            ArrayList<ValidityHash> v = getValidityHash("read.txt", i);
            for (ValidityHash x : v) {
                System.out.println(" vhash " + x.getHash());
            }
            System.out.println("");
        }
    }
}
//ab cd ab

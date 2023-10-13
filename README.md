# Almost Pure P2P Torrent

gTorr is a distributed p2p file sharing application aka torrent. This application uses just one central entity and gets rid of .torrent file which is used on [BitTorrent](https://en.wikipedia.org/wiki/BitTorrent) protocol.
Detailed design doc can be found [here](https://medium.com/@atharwaadawadkar448) 


# Setup

This application mainly has 3 entities
- **Tracker / Mapping DB** - This is a simple DB which store metadeta related to files to be downloaded.
- **Downloader / Leecher** - This acts like client who queries multiple peers to download a file.
-  **Uploader / Seeder** - This acts like server who upload multiple files chunk by chunk to downloader 


## Tracker 
This is simple mysql database which has following colomn
```
public class Tracker {
    @Id
    private String mFileHash;
    @Column
    private HashSet<String> mFileNames = new HashSet<>();
    @Column
    Long mFileSize;
    @Column // This is list of IPs who are seeding the file
    private HashSet<String> mHosts = new HashSet<>();
    @Column
    private String mMerkleTreeRoot;
 }
``` 
You don't need to create table upfront spring does that for you.
Just run mysql server and update DB name, IP and port [here](https://github.com/ashtnemi448/gTorr/blob/main/src/main/resources/application.properties#L1) 

## Seeder
cd inside source and start server
```
mvn clean package && mvn clean install
```

After app starts it picks all the files present in this directory, creates merkle tree for each file and stores it on disk.
Then metadata for each file along with IP addresses will be uploaded to tracker.

> While app initialization all the file hashes are printed on console, which can be used by downloader to start a download.

## Downloader

cd inside source and start server
```
mvn clean package && mvn clean install
```

Search for the FileHash that you want to download.
Then just fire a GET request to download file from all the seeders chunk by chunk
```
http://server:port/fileName/fileHash
```

package org.example;

import org.reflections.Store;
import org.rocksdb.*;
import org.rocksdb.util.SizeUnit;

import java.io.*;

public class BlockStore {

    Node node;
    byte[] RawBlock=new byte[1024*1024];
    byte[] Block;
    byte[][] BlockPart;
    byte[][] RawEncodeData;
    byte[][] EncodeData;
    byte[][][] EncodeBlock;
    byte[] StoreBlock;

    public BlockStore(Node node,int height) throws RocksDBException, IOException {
        this.node=node;
        RocksDBInit();
        for(int i=1;i<=height;i++) Store(i);
    }

    private void RocksDBInit() throws RocksDBException {
        final Options options = new Options();
        final Filter bloomFilter = new BloomFilter(10);
        final ReadOptions readOptions = new ReadOptions().setFillCache(false);
        final Statistics stats = new Statistics();
        final RateLimiter rateLimiter = new RateLimiter(10000000, 10000, 10);

        options.setCreateIfMissing(true)
                .setStatistics(stats)
                .setWriteBufferSize(8 * SizeUnit.KB)
                .setMaxWriteBufferNumber(3)
                .setMaxBackgroundJobs(10)
                .setCompressionType(CompressionType.SNAPPY_COMPRESSION)
                .setCompactionStyle(CompactionStyle.UNIVERSAL);

        final BlockBasedTableConfig table_options = new BlockBasedTableConfig();
        Cache cache = new LRUCache(64 * 1024, 6);
        table_options.setBlockCache(cache)
                .setFilterPolicy(bloomFilter)
                .setBlockSizeDeviation(5)
                .setBlockRestartInterval(10)
                .setCacheIndexAndFilterBlocks(true)
                .setBlockCacheCompressed(new LRUCache(64 * 1000, 10));
        options.setTableFormatConfig(table_options);
        options.setRateLimiter(rateLimiter);
        node.db = RocksDB.open(options, node.dbPath);
    }

    public void Store(int height) throws IOException, RocksDBException {
        BlockGen();
        BlockCompress();
        BlockCut();
        BlockEncode();
        BlockPartStore(height+"");
    }

    private void BlockPartStore(String height) throws IOException, RocksDBException {
        /*ByteArrayOutputStream baos=new ByteArrayOutputStream();
        for(int i=0;i<node.BlockPartStoredPerNode;i++){
            baos.write(EncodeBlock[i][node.Index]);
        }
        StoreBlock=baos.toByteArray();
        node.db.put(height.getBytes(),StoreBlock);*/
        if(node.Index<node.k){
            for(int i=0;i<node.BlockPartStoredPerNode;i++){
                String Index=height+"_"+i;
                node.db.put(Index.getBytes(),EncodeBlock[i][node.Index]);
            }
        }else{
            for(int i=0;i<node.BlockPartStoredPerNode;i++){
                String Index=height+"_"+i;
                node.db.put(Index.getBytes(),EncodeBlock[i][node.Index+37- node.k]);
            }
        }

    }

    private void BlockEncode() {

        EncodeBlock=new byte[node.BlockPartStoredPerNode][37+node.r][BlockPart[0].length];
        for(int i=0;i<node.BlockPartStoredPerNode;i++){
            RawEncodeData=new byte[37][BlockPart[0].length];
            int j;
            for(j=0;j<node.k;j++){
                if((node.k*i+j)==19)break;
                System.arraycopy(BlockPart[node.k*i+j],0,RawEncodeData[j],0,BlockPart[node.k*i+j].length);
                //RawEncodeData[j]=BlockPart[node.k*i+j];
                System.arraycopy(BlockPart[node.k*i+j],0,EncodeBlock[i][j],0,BlockPart[node.k*i+j].length);
                //EncodeBlock[i][j]=BlockPart[node.k*i+j];
            }
            /*for(;j<37;j++){
                RawEncodeData[j]= new byte[]{0};
                EncodeBlock[i][j]=new byte[]{0};
            }*/
            //区块编码，返回r个校验数据
            EncodeData=Encode(RawEncodeData);
            for(int l=0;l<node.r;l++){
                EncodeBlock[i][37+l]=EncodeData[l];
            }
        }
    }

    private byte[][] Encode(byte[][] Block) {
        byte[][] Coding=new byte[node.r][BlockPart[0].length];
        return Coding;
    }

    private void BlockCut() throws IOException {
        byte[] Part=new byte[Block.length/19+1];
        BlockPart= new byte[19][Part.length];
        ByteArrayInputStream bais=new ByteArrayInputStream(Block);
        //bais.read(Part);
        for(int i=0;i<19;i++){
            bais.read(Part);
            //BlockPart[i]=Part;
            System.arraycopy(Part,0,BlockPart[i],0,Part.length);
        }
        node.ShardSize=Part.length;
    }

    private void BlockCompress() throws IOException {
        Block=DEFLATE.compress(RawBlock);
    }

    private void BlockGen() throws IOException {
        File block=new File("D:\\idea_code\\Shi\\store/Block");
        FileInputStream fis=new FileInputStream(block);
        fis.read(RawBlock);
    }
}

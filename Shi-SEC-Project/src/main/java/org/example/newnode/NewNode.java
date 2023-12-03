package org.example.newnode;

import org.rocksdb.RocksDB;

import java.io.IOException;
import java.net.Socket;

public class NewNode {
    int Index;
    String IP=null;
    int Port;
    String Address;
    int height;
    //区块链集群参数
    int R,k,r,f,NodeSum,ShardNum,BlockPartStoredPerNode;
    int Status;
    //存储参数
    static final String dbPath = "./src/main/resources/data/";
    private static final String cfdbPath = "./src/main/resources/data-cf/";
    private static final String txdbPath = "./src/main/resources/data-tx/";
    RocksDB db;
    //读取参数
    int ReadRedundancy;

    String[] peersIp;
    Socket[] sockets;


    public NewNode(int NodeSum, int Index){
        this.NodeSum=NodeSum;
        this.Index=Index;
        this.peersIp=new String[]{"10.68.33.141:8080","10.68.33.141:8081","10.68.33.141:8082","10.68.33.141:8083","10.68.33.141:8084","10.68.33.141:8085","10.68.33.141:8086","10.68.33.141:8087","10.68.33.141:8088","10.68.33.141:8089","10.68.33.141:8090","10.68.33.141:8091","10.68.33.141:8092","10.68.33.141:8093","10.68.33.141:8094","10.68.33.141:8095","10.68.33.141:8096","10.68.33.141:8097","10.68.33.141:8098","10.68.33.141:8099","10.68.33.141:8100","10.68.33.141:8101","10.68.33.141:8102","10.68.33.141:8103","10.68.33.141:8104","10.68.33.141:8105","10.68.33.141:8106","10.68.33.141:8107","10.68.33.141:8108","10.68.33.141:8109","10.68.33.141:8110","10.68.33.141:8111","10.68.33.141:8112","10.68.33.141:8113","10.68.33.141:8114","10.68.33.141:8115","10.68.33.141:8116","10.68.33.141:8117","10.68.33.141:8118","10.68.33.141:8119"};
        String[] Addresses=peersIp[Index].split(":");
        this.IP=Addresses[0];
        this.Port=Integer.parseInt(Addresses[1]);
        this.Address=IP+Port;

        //
        this.R=3;
        if(this.NodeSum%this.R==0)this.r=(this.NodeSum/this.R-1)*2;
        else this.r=(this.NodeSum/this.R);
        this.f=this.r;
        this.k=this.NodeSum - this.r;
        if(this.NodeSum%this.R==0)Status=2;
        else Status=1;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        NewNode nn=new NewNode(4,4);
        nn.height=50;
        NewNodeServer nns=new NewNodeServer(nn);
        if(nn.Status==1) nns.BlockSynOne();
        else if (nn.Status==2) {
            nns.BlockSynTwo();
        }
    }
}

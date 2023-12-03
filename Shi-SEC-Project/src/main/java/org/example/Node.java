package org.example;

import org.rocksdb.*;

import java.net.Socket;

public class Node {
    //身份信息
    public int Index;
    String IP=null;
    public int Port;
    String Address=IP+Port;
    int height;
    //区块链集群参数
    public int R=3;
    int k;
    int r;
    int f;
    public int NodeSum;
    int ShardNum;
    public int BlockPartStoredPerNode,ShardSize;
    //存储参数
    //String dbPath = "/home/res/data";
    String dbPath = "D:\\idea_code\\Shi\\store/data";
    //String cfdbPath = "/home/res/data-cf";
    String cfdbPath = "D:\\idea_code\\Shi\\store/data-cf";
    //String txdbPath = "/home/res/data-tx";
    String txdbPath = "D:\\idea_code\\Shi\\store/data-tx";
    public RocksDB db;
    //读取参数
    int ReadRedundancy;

    String[] peersIp;
    Socket[] sockets;


    public Node(int NodeSum, int Index){
        this.NodeSum=NodeSum;
        this.Index=Index;
        if(this.NodeSum%this.R==0)this.r=(this.NodeSum/this.R-1)*2;
        else this.r=(this.NodeSum/this.R);
        this.f=this.r;
        this.k=this.NodeSum - this.r;
        this.peersIp=new String[]{"10.68.33.141:8080","10.68.33.141:8081","10.68.33.141:8082","10.68.33.141:8083","10.68.33.141:8084","10.68.33.141:8085","10.68.33.141:8086","10.68.33.141:8087","10.68.33.141:8088","10.68.33.141:8089","10.68.33.141:8090","10.68.33.141:8091","10.68.33.141:8092","10.68.33.141:8093","10.68.33.141:8094","10.68.33.141:8095","10.68.33.141:8096","10.68.33.141:8097","10.68.33.141:8098","10.68.33.141:8099","10.68.33.141:8100","10.68.33.141:8101","10.68.33.141:8102","10.68.33.141:8103","10.68.33.141:8104","10.68.33.141:8105","10.68.33.141:8106","10.68.33.141:8107","10.68.33.141:8108","10.68.33.141:8109","10.68.33.141:8110","10.68.33.141:8111","10.68.33.141:8112","10.68.33.141:8113","10.68.33.141:8114","10.68.33.141:8115","10.68.33.141:8116","10.68.33.141:8117","10.68.33.141:8118","10.68.33.141:8119"};
        //this.peersIp=new String[]{"172.30.197.84:8080","172.30.197.85:8080","172.30.197.86:8080","172.30.197.87:8080","172.30.197.88:8080"};
        //this.peersIp=new String[]{"172.30.197.107:8080","172.30.197.108:8080","172.30.197.109:8080","172.30.197.110:8080","172.30.197.111:8080","172.30.197.112:8080","172.30.197.113:8080","172.30.197.114:8080","172.30.197.115:8080","172.30.197.116:8080","172.30.197.117:8080","172.30.197.118:8080","172.30.197.119:8080","172.30.197.120:8080","172.30.197.121:8080","172.30.197.122:8080","172.30.197.123:8080","172.30.197.124:8080","172.30.197.125:8080","172.30.197.126:8080","172.30.197.127:8080","172.30.197.128:8080","172.30.197.129:8080","172.30.197.130:8080","172.30.197.131:8080","172.30.197.132:8080","172.30.197.133:8080"};
        String[] Address=peersIp[Index].split(":");
        this.IP=Address[0];
        this.Port=Integer.parseInt(Address[1]);
        sockets=new Socket[NodeSum];

        height=0;
        ShardNum=19;
        if(19%this.k==0) BlockPartStoredPerNode=1;
        else BlockPartStoredPerNode=19/this.k+1;
        //ShardSize=
        dbPath=dbPath+Index+"/";
        cfdbPath=cfdbPath+Index+"/";
        txdbPath=txdbPath+Index+"/";
    }
}

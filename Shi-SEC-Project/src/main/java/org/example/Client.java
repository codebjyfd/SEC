package org.example;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.Random;

import static org.apache.commons.lang3.RandomUtils.nextBoolean;

public class Client {
    int NodeSum;
    String[] peersIp;
    Socket[] Sockets;
    ClientGetBlock[] cgb;
    Thread[] GetBlock;
    int[] FirstSet,Right;
    byte[][] Block;
    public Client(int NodeSum) throws IOException {
        this.NodeSum=NodeSum;
        FirstSet=new int[NodeSum];
        this.peersIp=new String[]{"10.68.33.141:8080","10.68.33.141:8081","10.68.33.141:8082","10.68.33.141:8083","10.68.33.141:8084","10.68.33.141:8085","10.68.33.141:8086","10.68.33.141:8087","10.68.33.141:8088","10.68.33.141:8089","10.68.33.141:8090","10.68.33.141:8091","10.68.33.141:8092","10.68.33.141:8093","10.68.33.141:8094","10.68.33.141:8095","10.68.33.141:8096","10.68.33.141:8097","10.68.33.141:8098","10.68.33.141:8099","10.68.33.141:8100","10.68.33.141:8101","10.68.33.141:8102","10.68.33.141:8103","10.68.33.141:8104","10.68.33.141:8105","10.68.33.141:8106","10.68.33.141:8107","10.68.33.141:8108","10.68.33.141:8109","10.68.33.141:8110","10.68.33.141:8111","10.68.33.141:8112","10.68.33.141:8113","10.68.33.141:8114","10.68.33.141:8115","10.68.33.141:8116","10.68.33.141:8117","10.68.33.141:8118","10.68.33.141:8119"};
        Init();
    }

    public void Init() throws IOException {
        Sockets=new Socket[NodeSum];
        for(int i=0;i<NodeSum;i++){
            String[] peerIp=peersIp[i].split(":");
            String Ip=peerIp[0];
            int port=Integer.parseInt(peerIp[1]);
            Sockets[i] = new Socket(Ip, port);
        }
        cgb=new ClientGetBlock[NodeSum];
        GetBlock=new Thread[NodeSum];
    }

    public void getDistinctChunkSet(int[] chunk_set,int height){
        Random rd1 = new Random(height);

        for(int i=0;i<NodeSum;i++)
        {
            //int a=rd1.nextInt(8);
            chunk_set[i]=rd1.nextInt(NodeSum);
            for(int j=0;j<i;j++)
            {
                if(chunk_set[j]==chunk_set[i])
                {
                    i--;
                    break;
                }
            }
        }
    }

    public byte[] BlockRequest(int height) throws InterruptedException {
        getDistinctChunkSet(FirstSet,height);
        //todo：测试
        FirstSet[0]=1;FirstSet[1]=1;FirstSet[2]=1;FirstSet[3]=0;
        for(int i=0;i<NodeSum;i++){
            /*if(nextBoolean()==true)FirstSet[i]=1;
            else FirstSet[i]=0;*/
            if(FirstSet[i]==1) {
                cgb[i]=new ClientGetBlock(Sockets[i],1,height);
            } else if (FirstSet[i]==0) {
                cgb[i]=new ClientGetBlock(Sockets[i],2,height);
            }
            GetBlock[i]=new Thread(cgb[i]);
        }


        for(int i=0;i<NodeSum;i++){
            if(FirstSet[i]==1){
                GetBlock[i].start();
            }
        }
        int flag=1;
        Block=new byte[NodeSum][1048576];
        Right=new int[NodeSum];
        for(int i=0;i<NodeSum;i++){
            if(FirstSet[i]==1){
                GetBlock[i].join();
                if(cgb[i].BlockPart.length==0){
                    flag--;
                }else {
                    //Block=new byte[NodeSum][cgb[i].BlockPart.length];
                    Block[i]=cgb[i].BlockPart;
                    Right[i]=1;
                }
                if(flag<0){
                    for(int j=0;j<NodeSum;j++){
                        if(FirstSet[j]==0){
                            GetBlock[j].start();
                        }
                    }
                }
            }
        }
        if(flag<0){
            for(int i=0;i<NodeSum;i++){
                if(FirstSet[i]==2){
                    GetBlock[i].join();
                    if(cgb[i].BlockPart.length==0){
                        flag--;
                    }else {
                        Block[i]=cgb[i].BlockPart;
                        Right[i]=1;
                    }
                }
            }
        }
        return Decode(Block,Right);
    }

    private byte[] Decode(byte[][] block, int[] right) {
        return new byte[1];
    }


    public static void main(String[] args){
        byte[] block;
        try {
            Client c=new Client(4);
            //c.Init(11);
            block=c.BlockRequest(11);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println(block);
    }
}

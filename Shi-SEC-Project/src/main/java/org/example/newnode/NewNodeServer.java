package org.example.newnode;

import org.example.newnode.NewNode;

import java.io.*;
import java.net.Socket;

public class NewNodeServer {
    NewNode node;
    int normalNodeSum;
    int[] normalNode;
    BlockRequest[] br;
    Thread[] threads;
    byte[][] BlockParts;
    byte[][] StoreParts;
    public NewNodeServer(NewNode node) throws IOException {
        this.node=node;
        Init();
    }

    private void Init() throws IOException {
        node.sockets=new Socket[node.NodeSum];
        for(int i=0;i<node.NodeSum;i++){
            String[] peerIp=node.peersIp[i].split(":");
            String Ip=peerIp[0];
            int port=Integer.parseInt(peerIp[1]);
            node.sockets[i] = new Socket(Ip, port);
        }
        br=new BlockRequest[node.NodeSum];
        threads=new Thread[node.NodeSum];
        BlockParts=new byte[node.NodeSum][1048576];
    }

    public byte[] Merge(byte[][] byteArray){
        return new byte[1];
    }

    public void BlockSynOne() throws InterruptedException, IOException {
        for(int i=0;i<node.NodeSum;i++){
            br[i]=new BlockRequest(node.sockets[i]);
        }
        int he=1;
        while(he<=node.height){
            int flag=0;
            for(int i=0;i<node.NodeSum;i++){
                br[i].height=he;
                br[i].flag=1;
                threads[i]=new Thread(br[i]);
                threads[i].start();
            }
            for(int i=0;i<node.NodeSum;i++){
                threads[i].join();
                BlockParts[i]=br[i].BlockPart;
                if(br[i].BlockPart==null)flag++;
            }
            //if(flag>?)
            //todo:存储部分区块分片
            //发送部分区块分片至校验节点
            for(int i=node.k;i<node.NodeSum;i++){
                br[i].flag=2;
                br[i].SendParts=Merge(StoreParts);
                threads[i]=new Thread(br[i]);
                threads[i].start();
            }
            for(int i=node.k;i<node.NodeSum;i++){
                threads[i].join();
            }

            he++;
        }
        for(int i=0;i<node.NodeSum;i++){
            OutputStream os=node.sockets[i].getOutputStream();
            PrintWriter pw=new PrintWriter(os);
            pw.write("STOP\n");
            pw.close();
            os.close();
        }

    }

    public void BlockSynTwo() throws IOException, InterruptedException {
        normalNode=new int[node.NodeSum];
        for(int i=0;i<node.NodeSum;i++){
            br[i]=new BlockRequest(node.sockets[i]);
        }
        for(int i=0;i<node.NodeSum;i++){
            OutputStream os=node.sockets[i].getOutputStream();
            PrintWriter pw=new PrintWriter(os);
            InputStream is=node.sockets[i].getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            pw.write("ACK\n");
            pw.flush();
            String  ReceiveMessage = br.readLine();
            if(ReceiveMessage.equals("YES")){
                normalNodeSum++;
                normalNode[i]=1;
            }
            pw.close();
            br.close();
            isr.close();
            os.close();
            is.close();
        }
        int he=0,num;
        if(normalNodeSum%2==0)num=normalNodeSum/2;
        else num=normalNodeSum/2+1;
        while(he<node.height){
            for(int i=0,flag=0;i<node.NodeSum;i++){
                if(normalNode[i]==1){
                    flag++;
                    br[i].height=he;
                    br[i].flag=3;
                    threads[i]=new Thread(br[i]);
                    threads[i].start();
                }
                if(flag==num)break;
            }
            for(int i=0;i<node.NodeSum;i++){
                if(normalNode[i]==1){
                    threads[i].join();
                    BlockParts[i]=br[i].BlockPart;
                }
            }
            //todo:存储部分区块分片

            he++;
        }
        for(int i=0;i<node.NodeSum;i++){
            OutputStream os=node.sockets[i].getOutputStream();
            PrintWriter pw=new PrintWriter(os);
            pw.write("STOP\n");
            pw.close();
            os.close();
        }

    }

}

package org.example;

import org.rocksdb.RocksDBException;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientResponse {

    Node node;
    Socket socket=null;
    String ReceiveMessage,SendMessage;
    String[] ReceiveDataString;
    int height;
    public ClientResponse(Node node){
        this.node=node;
    }

    public void start() throws IOException, RocksDBException {
        ServerSocket serverSocket = new ServerSocket(node.Port);
        System.out.println("服务器即将启动，等待客户端的连接***"+node.IP+":"+node.Port);
        //调用accept()方法侦听，等待客户端的连接以获取Socket实例
        socket = serverSocket.accept();
        //创建新线程
        System.out.println("connect success");
        InetSocketAddress socketAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
        String clientIpAddress = socketAddress.getAddress().getHostAddress();
        InetAddress address = socket.getInetAddress();
        System.out.println("区块访问客户端IP：" + address.getHostAddress()+":"+socket.getPort());
        //TcpRead tr = new TcpRead(node, socket);
        BlockResponse();
    }

    private static byte[] mergeBytes(byte[]... values) {
        int lengthByte = 0;
        for (byte[] value : values) {
            lengthByte += value.length;
        }
        byte[] allBytes = new byte[lengthByte];
        int countLength = 0;
        for (byte[] b : values) {
            System.arraycopy(b, 0, allBytes, countLength, b.length);
            countLength += b.length;
        }
        return allBytes;
    }

    /*public void BlockResponse() throws IOException, RocksDBException {
        InputStream is = socket.getInputStream();
        InputStreamReader isr = new InputStreamReader(is, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        OutputStream os = socket.getOutputStream();
        PrintWriter pw = new PrintWriter(os);

        //输出流包装为打印流
        InetAddress ia = socket.getInetAddress();
        String clientIp = ia.getHostAddress();
        int clientPort = socket.getPort();
        while(true){
            String data = null;
            int i = 0;
            ReceiveMessage = br.readLine(); //循环读取客户端的信息
                //System.out.println("我是服务器，客户端提交信息为："+Data_String[i]);
            ReceiveDataString = ReceiveMessage.split("_");
            String messageType = ReceiveDataString[0];
            height=Integer.parseInt(ReceiveDataString[1]);
            if (messageType.equals("READ")) {
                //返回本地所有条带
//                ServerRead sr=new ServerRead(node,dataString,1);
//                String block=sr.start();
//                Client c=new Client(clientIp,clientPort,block);
//                c.start();
                byte[][] mergeArray = new byte[node.BlockPartStoredPerNode][node.ShardSize];
                byte[] BlockParts;
                for(int j=0;j<node.BlockPartStoredPerNode;j++){
                    mergeArray[j]=node.db.get((height+"_"+j).getBytes());
                }
                BlockParts=mergeArray[0];
                if(node.BlockPartStoredPerNode!=1){
                    for(int j=1;j<node.BlockPartStoredPerNode;j++){
                        BlockParts=mergeBytes(BlockParts,mergeArray[j]);
                    }
                }



                //向服务器端发送信息
                SendMessage=new String(BlockParts);
                SendMessage=node.Index+"_"+height+"_"+SendMessage;
                pw.write(SendMessage+"\nfwrobin\n");//写入内存缓冲区
                pw.flush();//刷新缓存，向服务器端输出信息
            }else if(messageType.equals("READOUT")) {
//                Client c = new Client(clientIp, clientPort, "FX");
//                c.start();
                //向服务器端发送信息
                byte[][] mergeArray = new byte[node.BlockPartStoredPerNode][node.ShardSize];
                byte[] BlockParts;
                for(int j=0;j<node.BlockPartStoredPerNode;j++){
                    mergeArray[j]=node.db.get((height+"_"+j).getBytes());
                }
                BlockParts=mergeArray[0];
                if(node.BlockPartStoredPerNode!=1){
                    for(int j=1;j<node.BlockPartStoredPerNode;j++){
                        BlockParts=mergeBytes(BlockParts,mergeArray[j]);
                    }
                }

                SendMessage=new String(BlockParts);
                //SendMessage=new String(node.db.get(Integer.toString(height).getBytes()));
                SendMessage=node.Index+"_"+height+"_"+SendMessage;
                pw.write(SendMessage+"\nfwrobin\n");//写入内存缓冲区
                pw.flush();//刷新缓存，向服务器端输出信息
            }else if(messageType.equals("STOP")){
                break;
            }
//            }else if(messageType.equals("DECODE")){
//                if(dataString[1]=="ALL"){
//                    //0:DECODE,1:ALL,2:Epoch,3:Index
//                    ServerRead sr=new ServerRead(node,dataString,2);
//                    String block=sr.start();
//                    Client c=new Client(clientIp,clientPort,block);
//                    c.start();
//                }else if(dataString[1]=="PART"){
//                    //0:DECODE,1:PART,2:int[n-f],3:Epoch,4:Index
//                    ServerRead sr=new ServerRead(node,dataString,3);
//                    String block=sr.start();
//                    Client c=new Client(clientIp,clientPort,block);
//                    c.start();
//                }
//            }
        }
        //socket.shutdownOutput();//关闭输出流
        //socket.shutdownInput();
        pw.close();
        os.close();
        br.close();
        isr.close();
        is.close();
        socket.close();
    }*/

    public void BlockResponse() throws IOException, RocksDBException {
        InputStream is = socket.getInputStream();
        InputStreamReader isr = new InputStreamReader(is, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        OutputStream os = socket.getOutputStream();
        //PrintWriter pw = new PrintWriter(os);

        //输出流包装为打印流
        InetAddress ia = socket.getInetAddress();
        String clientIp = ia.getHostAddress();
        int clientPort = socket.getPort();
        while(true){
            String data = null;
            int i = 0;
            ReceiveMessage = br.readLine(); //循环读取客户端的信息
            //System.out.println("我是服务器，客户端提交信息为："+Data_String[i]);
            ReceiveDataString = ReceiveMessage.split("_");
            String messageType = ReceiveDataString[0];
            height=Integer.parseInt(ReceiveDataString[1]);
            if (messageType.equals("READ")) {
                //返回本地所有条带
//                ServerRead sr=new ServerRead(node,dataString,1);
//                String block=sr.start();
//                Client c=new Client(clientIp,clientPort,block);
//                c.start();
                byte[][] mergeArray = new byte[node.BlockPartStoredPerNode][node.ShardSize];
                byte[] BlockParts;
                for(int j=0;j<node.BlockPartStoredPerNode;j++){
                    mergeArray[j]=node.db.get((height+"_"+j).getBytes());
                }
                BlockParts=mergeArray[0];
                if(node.BlockPartStoredPerNode!=1){
                    for(int j=1;j<node.BlockPartStoredPerNode;j++){
                        BlockParts=mergeBytes(BlockParts,mergeArray[j]);
                    }
                }



                //向服务器端发送信息
                //SendMessage=new String(BlockParts);
                //SendMessage=node.Index+"_"+height+"_"+SendMessage;
                os.write(BlockParts);//写入内存缓冲区
                os.flush();//刷新缓存，向服务器端输出信息
            }else if(messageType.equals("READOUT")) {
//                Client c = new Client(clientIp, clientPort, "FX");
//                c.start();
                //向服务器端发送信息
                byte[][] mergeArray = new byte[node.BlockPartStoredPerNode][node.ShardSize];
                byte[] BlockParts;
                for(int j=0;j<node.BlockPartStoredPerNode;j++){
                    mergeArray[j]=node.db.get((height+"_"+j).getBytes());
                }
                BlockParts=mergeArray[0];
                if(node.BlockPartStoredPerNode!=1){
                    for(int j=1;j<node.BlockPartStoredPerNode;j++){
                        BlockParts=mergeBytes(BlockParts,mergeArray[j]);
                    }
                }

                //SendMessage=new String(BlockParts);
                //SendMessage=new String(node.db.get(Integer.toString(height).getBytes()));
                //SendMessage=node.Index+"_"+height+"_"+SendMessage;
                os.write(BlockParts);//写入内存缓冲区
                os.flush();//刷新缓存，向服务器端输出信息
            }else if(messageType.equals("STOP")){
                break;
            }
//            }else if(messageType.equals("DECODE")){
//                if(dataString[1]=="ALL"){
//                    //0:DECODE,1:ALL,2:Epoch,3:Index
//                    ServerRead sr=new ServerRead(node,dataString,2);
//                    String block=sr.start();
//                    Client c=new Client(clientIp,clientPort,block);
//                    c.start();
//                }else if(dataString[1]=="PART"){
//                    //0:DECODE,1:PART,2:int[n-f],3:Epoch,4:Index
//                    ServerRead sr=new ServerRead(node,dataString,3);
//                    String block=sr.start();
//                    Client c=new Client(clientIp,clientPort,block);
//                    c.start();
//                }
//            }
        }
        //socket.shutdownOutput();//关闭输出流
        //socket.shutdownInput();
        //pw.close();
        os.close();
        br.close();
        isr.close();
        is.close();
        socket.close();
    }

}

package org.example;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class NodeNet {

    Node node;

    public NodeNet(Node node){
        this.node=node;
    }

    public void start() throws IOException {

        //后面的节点接收前面的节点所发的socket请求，本节点充当服务器


            ServerSocket serverSocket = new ServerSocket(node.Port);
            Socket socket = null;
            //记录连接过服务器端的数量
            int count=0;
            System.out.println("***服务器即将启动，等待客户端的连接***");
            for(int i=0;i<node.Index;i++){//循环侦听新的客户端的连接
                //调用accept()方法侦听，等待客户端的连接以获取Socket实例
                if(node.Index==0)continue;
                socket = serverSocket.accept();
                node.sockets[i]=socket;
                InetSocketAddress socketAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
                String clientIpAddress = socketAddress.getAddress().getHostAddress();
                count++;
                System.out.println("连接请求次数：" + count);
                InetAddress address = socket.getInetAddress();
                int port=socket.getPort();
                /*String nodeAddress=address.getHostAddress()+":"+port;
                String[] peerIp=node.peersIp[i].split(":");
                for(int j=0;j<peerIp.length;j++){
                    if(nodeAddress.equals(peerIp))
                }*/
                System.out.println("已和该客户端连接：" + address.getHostAddress()+":"+port);
            }


        //前面的节点向后面的节点发送socket请求，本节点充当客户端
        for(int i=node.Index;i<node.NodeSum;i++){
            if(node.Index+1==node.NodeSum)continue;
            String[] peerIp=node.peersIp[i].split(":");
            String Ip=peerIp[0];
            int port=Integer.parseInt(peerIp[1]);
            node.sockets[i]=new Socket();

            node.sockets[i].bind(new InetSocketAddress(node.Port));
            System.out.println("向其发送连接：" + Ip+":"+port);
            node.sockets[i].connect(new InetSocketAddress(Ip, port));
            //node.sockets[i] = new Socket(Ip, port);
        }


    }
}

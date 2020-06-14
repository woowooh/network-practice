import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {

    public static void main(String[] args) throws IOException, IOException {
        ServerSocket ss = new ServerSocket(5001); // 监听指定端口
        int dataLength = 65536;
        int dataSum = 512;
        System.out.println("server is running...");
        for (;;) {
            Socket sock = ss.accept();
            System.out.println("connected from " + sock.getRemoteSocketAddress());
            Thread h = new Handler(sock, dataLength, dataSum);
            h.start();
        }
    }
}
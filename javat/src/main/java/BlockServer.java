import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;


public class BlockServer {
    public static long time_before = System.currentTimeMillis();

    public static void handle(Socket s, int dataLength, int num) {
        try (InputStream input = s.getInputStream()) {
            try (OutputStream output = s.getOutputStream()) {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8));
                BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8), 65536);

                while (true) {
                     String r = readn(reader, dataLength);
                     if (r.equals("")) {
                         long now = System.currentTimeMillis();
                         double cost = (now - time_before) / 1000.0;
                         double liuliang = ((long)num * dataLength) / (1024.0 * 1024.0);
                         double average = liuliang / cost;
                         System.out.println("data sum is: " + liuliang + " MB/s ");
                         System.out.println("time cost is: " + cost);
                         System.out.println("average: " + average + " MB/s");
                         System.out.println(c);
                         break;
                     }
                     if (r.charAt(dataLength - 1) == '\n') {
                         System.out.println("call ok");
                         writer.write("ok\n");
                         writer.flush();
                     }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("client disconnected.");
        }
    }

    static int c = 0;
    public static String readn(BufferedReader reader, int n) throws IOException {
        System.out.println("before");
        char[] chars = new char[n];
        int size = 0;
        int readSize = 0;
        StringBuilder sb = new StringBuilder();
        while (readSize < n) {
            size = reader.read(chars);
            if (size == -1 ) {
                break;
            }
            sb.append(Arrays.copyOf(chars, size));
            readSize += size;
            c++;
            chars = new char[n - readSize];
        }
        System.out.println("after");
        return sb.toString();
    }

    public static void main(String[] args) throws IOException, IOException {
        ServerSocket ss = new ServerSocket(5001); // 监听指定端口
        int dataLength = 20;
        int dataSum = 8192;
        System.out.println("server is running...");
        for (;;) {
            Socket sock = ss.accept();
            time_before = System.currentTimeMillis();
            System.out.println("connected from " + sock.getRemoteSocketAddress());
            handle(sock, dataLength, dataSum);
        }
    }
}


// 想验证 reader.read(chars) 如果 chars.size 大于可读数据的情况下，为什么没阻塞？  
// 调用链 fill() -> InputStreamReader::read(cbuf, offset, length)-> StreamDecoder::read(char[] var1, int var2, int var3)-> readBytes()-> SocketInputStream::socketRead0
// openjdk\jdk\src\solaris\native\java\net Java_java_net_SocketInputStream_socketRead0:: NET_Read(fd, bufP, len)
// 应该是 TCP栈从网卡收到数据后，如果有数据，则将获取的数据拷贝至应用层(实际收到的数据大小
// 如果对应链接一直没收到数据，则挂起该链接，应用层表现为阻塞至 read 调用，直到有数据到达链接被唤醒
// 太久没研究容易忘...
// thanks to https://my.oschina.net/u/4367103/blog/4335872

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;


public class Server {
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
        return sb.toString();
    }

    public static void main(String[] args) throws IOException, IOException {
        ServerSocket ss = new ServerSocket(5001); // 监听指定端口
        int dataLength = 1024000;
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
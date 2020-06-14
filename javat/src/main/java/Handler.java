import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Handler extends Thread {
    private static ThreadLocal<Long> time_before = new ThreadLocal<>();
    Socket sock;
    int dataLength;
    int num;

    public Handler(Socket s, int dataLength, int num) {
        this.sock = s;
        this.dataLength = dataLength;
        this.num = num;
    }

    @Override
    public void run() {
        this.handle(this.sock, this.dataLength, this.num);
    }

    public void handle(Socket s, int dataLength, int num) {
        time_before.set(System.currentTimeMillis());
        try (InputStream input = s.getInputStream()) {
            try (OutputStream output = s.getOutputStream()) {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8));
                BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8), 65536);

                while (true) {
                    String r = readn(reader, dataLength);
                    if (r.equals("")) {
                        long now = System.currentTimeMillis();
                        double cost = (now - time_before.get()) / 1000.0;
                        double liuliang = ((long)num * dataLength) / (1024.0 * 1024.0);
                        double average = liuliang / cost;
                        System.out.println("data sum is: " + liuliang + " MB/s ");
                        System.out.println("time cost is: " + cost);
                        System.out.println("average: " + average + " MB/s");
                        System.out.println();
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
            chars = new char[n - readSize];
        }
        return sb.toString();
    }
}

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


class Client extends Thread {
    String host;
    int port;
    int dataLength;
    int dataSum;

    public Client(String host, int port, int dataLength, int dataSum) {
        this.host = host;
        this.port = port;
        this.dataLength = dataLength;
        this.dataSum = dataSum;
    }

    public Socket createClient(String host, int port) throws IOException {
        Socket s = new Socket();
        s.setTcpNoDelay(false);
        s.connect(new InetSocketAddress(host, port));
        return s;
    }


    public void sendData(BufferedWriter os, String data) throws IOException {
        os.write(data);
        os.flush();
    }

    public boolean receiveData(BufferedReader s) throws IOException {
        String r = s.readLine();
        return r.equals("ok");
    }

    public String createString(int length) {
        StringBuilder r = new StringBuilder();
        for (int i = 0; i < length - 1; i++) {
            r.append("a");
        }
        r.append("\n");
        return r.toString();
    }

    @Override
    public void run() {
        Socket s = null;
        try {
            s = createClient(host, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String data = createString(dataLength);
        try (InputStream input = s.getInputStream()) {
            try (OutputStream output = s.getOutputStream()) {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8));
                BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
                for (int i = 0; i < dataSum; i++) {
                    sendData(writer, data);
                    boolean f = receiveData(reader);
                    if (!f) {
                        System.out.println("not ok");
                        break;
                    }
                }
                input.close();
                output.close();
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        // 千兆带宽 112MB 左右
        // 每条链路 1.2 MB
        // 100 条链路
        int threadNum = 100;
        Thread[] tl = new Thread[threadNum];
        String host = "192.168.199.215";
        int port = 5001;
        int dataLength = 65536;
        int dataSum = 512;
        for (int i = 0; i < threadNum; i++) {
            Thread t = new Client(host, port, dataLength, dataSum);
            t.start();
            tl[i] = t;
        }
        while (true) {
            Thread.sleep(1000);
        }

        /*
        *    数据长度 4096 数据数量 4096。 总大小 16MB
        *    单链路环境下测试传输量为 1.2 MB/s
        *    后开 100 线程测试，同样条件。
        *    应用层传输量降为 0.08 MB/s。100 条线路可理解为 8MB/s
        *    Java 环境下， 包大，数量小，测出带宽较高
        * */
    }
}

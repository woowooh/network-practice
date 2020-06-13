import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


class Client {
    public static Socket createClient(String host, int port) throws IOException {
        Socket s = new Socket();
        s.setTcpNoDelay(false);
        s.connect(new InetSocketAddress(host, port));
        return s;
    }


    public static void sendData(BufferedWriter os, String data) throws IOException {
        os.write(data);
        os.flush();
    }

    public static boolean receiveData(BufferedReader s) throws IOException {
        String r = s.readLine();
        return r.equals("ok");
    }

    public static String createString(int length) {
        StringBuilder r = new StringBuilder();
        for (int i = 0; i < length - 1; i++) {
            r.append("a");
        }
        r.append("\n");
        return r.toString();
    }

    public static void main(String[] args) throws IOException {
        String host = "localhost";
        int port = 5001;
        Socket s = createClient(host, port);
        int dataLength = 1024000;
        int dataSum = 8192;
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
            }
        }
    }
}

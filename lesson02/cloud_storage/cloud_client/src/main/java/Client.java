import java.io.*;
import java.net.Socket;

public class Client {

    private static Socket socket;
    private static DataInputStream is;
    private static DataOutputStream os;

    public static void sendFile(Socket socket, File file) throws IOException {
        InputStream is = new FileInputStream(file);
        long size = file.length();
        int count = (int) (size / 4096) / 10, readBuckets = 0;

        try(DataOutputStream os = new DataOutputStream(socket.getOutputStream())) {
            byte [] buffer = new byte[4096];
            os.writeUTF(file.getName());
            System.out.print("/");
//            while (is.available() > 0) {
//                int readBytes = is.read(buffer);
//                readBuckets++;
//                if (readBuckets % count == 0) {
//                    System.out.print("=");
//                }
//                os.write(buffer, 0, readBytes);
//            }
//            System.out.println("/");
            int readByte;
            while ((readByte = is.read(buffer)) != -1) {
                readBuckets++;
                if (readBuckets % count == 0) {
                    System.out.print("=");
                }
                os.write(buffer, 0, readByte);
            }
            System.out.println("/");
        }
    }

    public static void downloadFile(String fileName) throws IOException {
        os = new DataOutputStream(socket.getOutputStream());   // не пойму что не так.. Выдает здесь Exception in thread "main" java.lang.NullPointerException
        os.writeUTF("./cloud_client/src/main/resources/" + fileName);
        File file = new File(fileName);
        long size = file.length();
        int count = (int) (size / 4096) / 10, readBuckets = 0;

        try (InputStream is = new FileInputStream(file)) {
            byte [] buffer = new byte[4096];
            System.out.print("/");
            while (is.available() > 0) {
                int readBytes = is.read(buffer);
                readBuckets++;
                if (readBuckets % count == 0) {
                    System.out.print("=");
                }
                os.write(buffer, 0, readBytes);
            }
            System.out.println("/");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {

        sendFile(new Socket("localhost", 8189), new File("./cloud_client/src/main/resources/Java_Полное_руководство_Герберт_Шилдт.pdf"));
        sendFile(new Socket("localhost", 8189), new File("./cloud_client/src/main/resources/Философия Java. 4-е полное издание.djvu"));
        sendFile(new Socket("localhost", 8189), new File("./cloud_client/src/main/resources/Шилдт Г. - Java 8. Руководство для начинающих - 2015.pdf"));
        downloadFile("./cloud_server/src/main/resources/Java Game Development with LibGDX From Beginner to Professional.pdf");
        downloadFile("./cloud_server/src/main/resources/IMG_4439.JPG");

    }
}

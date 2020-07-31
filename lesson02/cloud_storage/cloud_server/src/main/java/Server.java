import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {

    private int acceptedClientIndex = 0;

    public Server() {

        try (ServerSocket serverSocket = new ServerSocket(8189)) {

            System.out.println("Server started... Waiting for clients...");
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected " + socket.getInetAddress() + " " + socket.getPort() + " " + socket.getLocalPort() + " id" + ++acceptedClientIndex);
                new ClientHandler(this, socket);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void uploadFile(Socket socket) throws IOException {
        DataInputStream is = new DataInputStream(socket.getInputStream());

        String fileName = is.readUTF();
        System.out.println("fileName: " + fileName);
        File path = new File("./cloud_server/src/main/resources/" + "id" + acceptedClientIndex + "/");

        if (!path.exists()) {
            path.mkdir();
        }
        File file = new File(path + "/" + fileName);
        if (!file.exists()){
            file.createNewFile();
        }

        try (FileOutputStream fos = new FileOutputStream(file)) {
            byte[] buffer = new byte[4096];
            int readByte;
            while ((readByte = is.read(buffer)) != -1)
                fos.write(buffer, 0, readByte);
        }
        System.out.println("File uploaded!");

    }

}

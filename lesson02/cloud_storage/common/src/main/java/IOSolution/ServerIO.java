package IOSolution;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerIO {

    private boolean isRunning = true;

    private void stop() {
        isRunning = false;
    }

    public ServerIO(){
        try {
            try(ServerSocket server = new ServerSocket(8189)) {
                System.out.println("Server started!");
                while (isRunning) {
                    Socket connection = server.accept();
                    System.out.println("Client accepted!");
                    new Thread(new FileHandler(connection)).start();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ServerIO();
    }
}

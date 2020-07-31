package IOSolution;

import java.io.*;
import java.net.Socket;

public class FileHandler implements Runnable{

    private String serverFilesPath = "./common/src/main/resources/serverFiles";
    private String serverFilesPath2 = "./common/src/main/resources/serverFiles";
    private Socket socket;
    private DataInputStream is;
    private DataOutputStream os;
    private boolean isRunning = true;
    private static int cnt = 1;

    public FileHandler(Socket socket) throws IOException {
        this.socket = socket;
        is = new DataInputStream(socket.getInputStream());
        os = new DataOutputStream(socket.getOutputStream());
        String userName = "user" + cnt;
        cnt++;
        serverFilesPath += "/" + userName;
        File dir = new File(serverFilesPath);
        if (!dir.exists()) {
            dir.mkdir();
        }

    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                String command = is.readUTF(); // принимаем посылаемые команды от клиента
                System.out.println(command);
                if (command.equals("./download")) {

                    String fileName = is.readUTF();
                    System.out.println("Find file with name: " + fileName);
                    File file = new File(serverFilesPath + "/" + fileName);

                    if (file.exists()) {

                        os.writeUTF("OK");
                        long length = file.length(); // запишем длину файла в переменную length
                        os.writeLong(length); // передадим длину файла клиенту
                        FileInputStream fis = new FileInputStream(file);
                        byte[] buffer = new byte[4096];
                        while (fis.available() > 0) {   //передаем байты самого файла

                            int count = fis.read(buffer);
                            os.write(buffer, 0, count);
                        }

                    } else {
                        os.writeUTF("File not exists");
                    }
                    
                } else if (command.equals("./upload")) {

                    String fileName = is.readUTF();

                    File file = new File(serverFilesPath + "/" + fileName);
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        byte[] buffer = new byte[4096];
                        int readByte;
                        while ((readByte = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, readByte);
                        }
                    }
                    System.out.println("File uploaded!");


                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}

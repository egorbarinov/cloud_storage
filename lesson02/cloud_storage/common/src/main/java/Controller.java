import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    public ListView<String> lv;
    public TextField txt;
    public Button send;
    private Socket socket;
    private DataInputStream is;
    private DataOutputStream os;
    private List<File> clientFileList;

    private final String clientFilesPath = "./common/src/main/resources/clientFiles";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            socket = new Socket("localhost",8189);
            is = new DataInputStream(socket.getInputStream());
            os =new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        File dir = new File(clientFilesPath);
        for (String file : dir.list()) {
            lv.getItems().add(file);
        }

    }

    public void sendCommand(ActionEvent actionEvent) throws IOException {

        String command = txt.getText();
        System.out.println(txt.getText());
        String[] op = command.split(" ");
        if (op[0].equals("./download")) {  // ./download fileName
            try {
                os.writeUTF(op[0]);
                os.writeUTF(op[1]);
                String response = is.readUTF(); // ожидаем ответ от сервера, все ок или нет /// переделать через байты//
                System.out.println("resp: " + response); // выведем ответ на экран
                if (response.equals("OK")) {
                    File file = new File(clientFilesPath + "/" + op[1]);
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    long length = is.readLong(); // читаем поступившую информацию о длине файла от сервера // переделать через байты
                    byte[] buffer = new byte[1024];
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        if (length < 1024) {
                            int count = is.read(buffer);
                            fos.write(buffer, 0, count);

                        } else {
                            for (long i = 0; i < length / 1024; i++) {
                                int count = is.read(buffer);
                                fos.write(buffer, 0, count);
                            }
                        }
                    }
                    lv.getItems().add(op[1]);
                    //txt.clear(); //////
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (op[0].equals("./upload")) {
            os.writeUTF("./upload");

            //txt.clear();

            File file = new File(clientFilesPath + "/" + op[1]);
            System.out.println(op[1]);
            //FileInputStream fis = new FileInputStream(file);

            try(DataOutputStream os = new DataOutputStream(socket.getOutputStream())) {
                FileInputStream fis = new FileInputStream(file);
                byte [] buffer = new byte[1024];
                os.writeUTF(file.getName());

                int readBytes;
                while ((readBytes = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, readBytes);
                }

            }
            //txt.clear(); //////
        }
    }
}

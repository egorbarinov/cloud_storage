package protocol_file_cloud_client;

import protocol_file_common.ProtoFileSender;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;

public class ProtoClientController implements Initializable {
    @FXML
    public Button sendButton;
    @FXML
    public Button receiveButton;
    @FXML
    public ListView<String> clientFilesList;
    @FXML
    public ListView<String> serverFilesList;  // должны получать от сервера при подключении к нему

   // private final Path path = Paths.get("./client_storage/");
    private final String filePath = "./client_storage/";
    private String serverFilePath;

    @FXML
    public void pressOnSendToCloudButton(ActionEvent actionEvent) throws IOException, InterruptedException {
        String fileToSend = filePath + clientFilesList.getSelectionModel().getSelectedItem();
        ProtoFileSender.sendFile(Paths.get(fileToSend), Network.getInstance().getCurrentChannel(), future -> {
            if (!future.isSuccess()) {
                future.cause().printStackTrace();
            }
            if (future.isSuccess()) {
                System.out.println("Файл успешно передан");
            }
        });
        serverFilesList.getItems().add(clientFilesList.getSelectionModel().getSelectedItem()); // ?? при отправке файла на сервер ожидать от сервера обновленный лист с файлами
        Thread.sleep(10);

    }

    // прошу прокомментировать использование ProtoFileSender в методе ниже, уместно ли его использование на стороне сервера для скачивания с него файлов на клиент???
    @FXML
    public void pressOnReceiveFile(ActionEvent actionEvent) throws IOException, InterruptedException {
        String fileToSend = serverFilePath + serverFilesList.getSelectionModel().getSelectedItem();
        ProtoFileSender.sendFile(Paths.get(fileToSend), Network.getInstance().getCurrentChannel(), future -> {
            if (!future.isSuccess()) {
                future.cause().printStackTrace();
            }
            if (future.isSuccess()) {
                System.out.println("Файл успешно принят");

            }
        });
        clientFilesList.getItems().add(serverFilesList.getSelectionModel().getSelectedItem());
        Thread.sleep(10);
    }

    private void refreshLocalFilesList() {

        try {
            clientFilesList.getItems().clear();
            Files.list(Paths.get("./client_storage/")).map(p -> p.getFileName().toString()).forEach(o -> clientFilesList.getItems().add(o));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void refreshServerFilesList(ArrayList<String> filesList) { // TODO реализовать прием от сервера листа с файлами
        serverFilesList.getItems().clear();
        serverFilesList.getItems().addAll(filesList);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CountDownLatch networkStarter = new CountDownLatch(1);
        new Thread(() -> Network.getInstance().start(networkStarter)).start();
        try {
            networkStarter.await();
            refreshLocalFilesList();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}

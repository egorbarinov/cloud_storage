package com.geekbrains.client.ui.dialogs.rename;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class GFileRenameDialog {
    private Stage stage;
    private String text;

    @FXML
    public TextField rdText;

    public GFileRenameDialog() {
        this.stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/FileRenameDialog.fxml"));
        fxmlLoader.setController(this);
        try {
            this.stage.setScene(new Scene(fxmlLoader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.stage.setTitle("RenameFileDialog");
        this.stage.setResizable(false);
        this.stage.initModality(Modality.APPLICATION_MODAL);
    }

    @FXML
    public void rdOKAction(ActionEvent actionEvent) {
        if (!this.rdText.getText().equals("")) {
            this.text = this.rdText.getText();
        }
        this.stage.close();
    }

    @FXML
    public void rdCancelAction(ActionEvent actionEvent) {
        this.stage.close();
    }

    public String showAndWait(String oldName) {
        this.text = oldName;
        this.rdText.setText(this.text);
        this.stage.showAndWait();
        return this.text;
    }

}

package my.backup.backupTool.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import my.backup.backupTool.MessageTYPE;
import my.backup.backupTool.Service.IMessageList;

public class MessageController implements IMessageController{

    @FXML
    private AnchorPane rootPane;
    @FXML
    private VBox container;

    @FXML
    private Label messageType;

    public void show(IMessageList messages, MessageTYPE Type) {
        messageType.setText(Type.toString());
        for (String message : messages.getMessagesAsList()) {
            Label messageLabel = new Label(message);
            messageLabel.getStyleClass().add("messageContent");
            container.getChildren().add(messageLabel);
        }

    }

    @FXML
    private void closeStage(){
       Stage stage = (Stage) rootPane.getScene().getWindow();
       stage.close();
    }




}

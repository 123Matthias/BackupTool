package my.backup.backupTool.Controller;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import my.backup.backupTool.Main;
import my.backup.backupTool.MessageTYPE;
import my.backup.backupTool.Service.IMessageList;

public class MessageController{

    @FXML
    private AnchorPane rootPane;
    @FXML
    private VBox container;

    @FXML
    private Label messageType;

    @FXML
    private AnchorPane toastContainer;

    public void showMessage(IMessageList messages, MessageTYPE Type) {
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

    public void showToast(String toastMessage) {
        HBox toastLayout = new HBox();

        ImageView logo = new ImageView(String.valueOf(Main.class.getResource("img/Fee.PNG")));
        logo.getStyleClass().add("toastLogo");
        logo.setFitHeight(100);
        logo.preserveRatioProperty().set(true);
        Label toastLabel = new Label(toastMessage);
        toastLabel.getStyleClass().add("toastLabel");
        toastLayout.getChildren().addAll(logo, toastLabel);

        AnchorPane.setTopAnchor(toastLayout, 0.0);
        AnchorPane.setBottomAnchor(toastLayout, 0.0);
        AnchorPane.setLeftAnchor(toastLayout, 0.0);
        AnchorPane.setRightAnchor(toastLayout, 0.0);
        toastLayout.setAlignment(Pos.CENTER_LEFT);

        toastContainer.getChildren().add(toastLayout);

    }




}

package my.backup.backupTool.Service;

import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import my.backup.backupTool.App;
import my.backup.backupTool.Controller.IMessageController;
import my.backup.backupTool.Controller.MessageController;
import my.backup.backupTool.MessageTYPE;
import my.backup.backupTool.SceneBuilder;

import java.io.IOException;

public class MessageService {


    public static void createMessage(IMessageList messages, MessageTYPE messageType) {

        Stage stage = new Stage();
        SceneBuilder sceneBuilder = App.Router.createMessage(App.Router.getTheme().toString());

        Scene scene = null;
        try {
            scene = sceneBuilder.getScene();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        MessageController controller = sceneBuilder.getController();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Validation");
        stage.setScene(scene);
        stage.show();
        controller.show(messages, messageType);

    }


}

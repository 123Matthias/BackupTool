package my.backup.backupTool.Service;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;
import my.backup.backupTool.App;
import my.backup.backupTool.Controller.MessageController;
import my.backup.backupTool.Controller.MessageTYPE;
import my.backup.backupTool.SceneBuilder;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class MessageService {

    private static Stage currentStage;
    private static FadeTransition currentFadeOut;
    private static PauseTransition currentPause;

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
        stage.setTitle(MessageTYPE.VALIDATION.toString());
        stage.setScene(scene);
        stage.show();
        controller.showMessage(messages, messageType);
    }



    public static void createToast(String toastText, MessageTYPE messageType) {
        if(currentStage != null) {
            stopCurrentToast();
        }
        Platform.runLater(() -> runPlatformThread(toastText, messageType));

    }

    private static void stopCurrentToast() {
        if (currentStage != null) {
            if (currentFadeOut != null) {
                currentFadeOut.stop();
                currentFadeOut = null;
            }
            if (currentPause != null) {
                currentPause.stop();
                currentPause = null;
            }
            currentStage.close();
            currentStage = null;
        }
    }

    private static void runPlatformThread(String toastText, MessageTYPE messageType){


        currentStage = App.Router.getToastStage();
        SceneBuilder sceneBuilder = App.Router.createToast(App.Router.getTheme().toString());

        Scene scene = null;
        try {
            scene = sceneBuilder.getScene();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        MessageController controller = sceneBuilder.getController();

        currentStage.setScene(scene);
        controller.showToast(toastText, messageType);

        scene.setFill(Color.TRANSPARENT);

        // Positioniere die Toast-Stage mittig zur Main-Stage
        centerStageOnAnother(currentStage, App.Router.getMainStage());

        currentPause = new PauseTransition(Duration.millis(2500));
        currentFadeOut = new FadeTransition(Duration.millis(2000), currentStage.getScene().getRoot());


        // Animationen
        currentFadeOut.setFromValue(1.0);
        currentFadeOut.setToValue(0.0);


        currentPause.setOnFinished(event -> {
            currentFadeOut.play();
        });

        currentFadeOut.setOnFinished(event -> {
            currentStage.close();
        });

        currentStage.show();
        currentPause.play();
    }



    private static void centerStageOnAnother(Stage stageToCenter, Stage referenceStage) {

        // Verhindern, dass die Stage sofort sichtbar wird (unsichtbar machen)
        stageToCenter.setOpacity(0);
        // Zeige die Stage vorübergehend an, um ihre Größe zu ermitteln
        stageToCenter.show();

        double referenceWidth = referenceStage.getWidth();
        double referenceHeight = referenceStage.getHeight();
        double referenceX = referenceStage.getX();
        double referenceY = referenceStage.getY();

        double stageWidth = stageToCenter.getWidth();
        double stageHeight = stageToCenter.getHeight();

        double stageX = referenceX + (referenceWidth - stageWidth) / 2;
        double stageY = referenceY + (referenceHeight - stageHeight) / 2;

        stageToCenter.setX(stageX);
        stageToCenter.setY(stageY);

        // Jetzt die Stage sichtbar machen
        stageToCenter.setOpacity(1);
    }
}

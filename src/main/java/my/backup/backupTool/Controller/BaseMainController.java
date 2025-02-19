package my.backup.backupTool.Controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import my.backup.backupTool.App;
import my.backup.backupTool.Model.BaseModel;

import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseMainController {

    private boolean isShaking = false; // Flag, um den Shake-Status zu überwachen
    private Timeline shakeTimeline; // Die Timeline, um das Zittern zu steuern


    public void enableDragAndDrop(Pane cardPane, FlowPane container) {


        // Drag-Start: Initialisiert den Drag-Vorgang
        cardPane.setOnDragDetected(event -> {
            cardPane.getChildren().get(0).getStyleClass().add("cardColDragDrop");
            Dragboard db = cardPane.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(cardPane.getId()); // Speichert die ID der gezogenen Karte
            db.setContent(content);
            event.consume();
        });

        // Drag-Überprüfung: Ermöglicht das Ablegen über anderen Karten
        cardPane.setOnDragOver(event -> {

            for(Node n : container.getChildren()){

                Pane p = (Pane) n;
                p.getChildren().get(0).getStyleClass().add("cardCol");
                p.getChildren().get(0).getStyleClass().remove("cardColDragDrop");
                Object gesSource = event.getGestureSource();
                if (gesSource instanceof Pane) {
                    Pane pa = (Pane) gesSource;

                    // Überprüfe, ob das Zittern noch nicht läuft, bevor es gestartet wird
                    if (!isShaking) {
                        // Auf Platform.runLater() setzen, um UI-Updates im richtigen Thread auszuführen
                        Platform.runLater(() -> {
                            shake(pa);  // Starte den Shake
                        });
                    }
                }

            }

            if (event.getGestureSource() != cardPane && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
                Object objTarget = event.getGestureTarget();
                if(objTarget instanceof Pane){
                    Pane p = (Pane) objTarget;
                    p.getChildren().get(0).getStyleClass().add("cardColDragDrop");
                }
            }




            event.consume();
        });


        // Drop-Vorgang: Verschiebt die Karte an die neue Position
        cardPane.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();

            if (db.hasString()) {
                Pane draggedCard = (Pane) container.lookup("#" + db.getString());
                if (draggedCard != null) {
                    int dropIndex = container.getChildren().indexOf(cardPane);
                    container.getChildren().remove(draggedCard);
                    container.getChildren().add(dropIndex, draggedCard);

                    // Aktualisiert die Reihenfolge im Datenmodell
                    List<BaseModel> models = container.getChildren().stream()
                            .map(node -> App.DataStore.getModelById(node.getId()))
                            .collect(Collectors.toList());

                    for (int i = 0; i < models.size(); i++) {
                        models.get(i).setFlowPanePosition(i);
                        App.DataStore.getModelById(models.get(i).getUid()).setFlowPanePosition(i);
                    }
                }
            }

            Object obpSource = event.getGestureSource();
            Object objTarget = event.getGestureTarget();
            if(objTarget instanceof Pane paneTarget){
                System.out.println(paneTarget);
                paneTarget.getChildren().get(0).getStyleClass().remove("cardColDragDrop");
                paneTarget.getChildren().get(0).getStyleClass().add("cardCol");

            }
            if(obpSource instanceof Pane paneSource){
                System.out.println(paneSource);
                paneSource.getChildren().get(0).getStyleClass().remove("cardColDragDrop");
                paneSource.getChildren().get(0).getStyleClass().add("cardCol");
            }
            this.stopShaking();
            App.DataStore.saveModelListAsJSON();
            event.consume();

        });

        cardPane.setOnDragExited(event -> {
            this.stopShaking();
        });
    }

    private void shake(Pane pane) {
        if (!isShaking) {
            isShaking = true;  // Shake-Status auf true setzen

            // Erstelle eine Timeline für das Zittern auf X- und Y-Achse
            shakeTimeline = new Timeline(
                    new KeyFrame(Duration.millis(60), e -> {
                        pane.setTranslateX(-1);  // Bewege es nach links
                        pane.setTranslateY(1);   // Bewege es nach oben
                    }),
                    new KeyFrame(Duration.millis(120), e -> {
                        pane.setTranslateX(1);   // Bewege es nach rechts
                        pane.setTranslateY(-1);  // Bewege es nach unten
                    }),
                    new KeyFrame(Duration.millis(180), e -> {
                        pane.setTranslateX(-1);  // Bewege es nach links
                        pane.setTranslateY(1);   // Bewege es nach oben
                    }),
                    new KeyFrame(Duration.millis(240), e -> {
                        pane.setTranslateX(1);   // Bewege es nach rechts
                        pane.setTranslateY(-1);  // Bewege es nach unten
                    })
            );

            // Setze die Timeline auf unendlich
            shakeTimeline.setCycleCount(Timeline.INDEFINITE);
            shakeTimeline.play();  // Starte die Animation
        }
    }

    private void stopShaking() {
        if (isShaking && shakeTimeline != null) {
            shakeTimeline.stop();  // Stoppe die Timeline
            isShaking = false;  // Setze das Flag zurück
        }
    }

}

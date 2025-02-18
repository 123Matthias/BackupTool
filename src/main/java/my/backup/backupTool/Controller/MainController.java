package my.backup.backupTool.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.ToolBar;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import my.backup.backupTool.App;
import my.backup.backupTool.Controller.Merge.MergeDetailController;
import my.backup.backupTool.Controller.Merge.MergeHelperController;
import my.backup.backupTool.Model.BaseModel;

public class MainController {

    @FXML
    private ToolBar toolBar;

    @FXML
    private FlowPane cardContainer;

    private MergeHelperController mergeHelperController;

    @FXML
    public void initialize(){
        mergeHelperController = new MergeHelperController(this);
        mergeHelperController.addAllCardsSorted(App.DataStore.getModelList());
    }


    @FXML
    public void backToMain(){
        //  Main.mainStage.setScene(Main.sceneMain);
        App.Router.getMainStage().setScene(App.Router.getMainScene());
        App.Router.getMainStage().show();
    }

    public void enableDragAndDrop(Pane card, FlowPane container) {
        // Drag startet
        card.setOnDragDetected(event -> {
            Dragboard db = card.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(card.getId()); // ID zur Identifikation
            db.setContent(content);
            event.consume();
        });

        // Drag bewegt sich über eine andere Card und ist nicht die eigene Card != card
        card.setOnDragOver(event -> {
            if (event.getGestureSource() != card && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        // Drag wird fallen gelassen
        card.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                String draggedId = db.getString();
                Pane draggedCard = (Pane) container.lookup("#" + draggedId); // Suche Karte
                if (draggedCard != null) {
                    int dropIndex = container.getChildren().indexOf(card); // Zielposition
                    container.getChildren().remove(draggedCard); // Alte Karte entfernen
                    container.getChildren().add(dropIndex, draggedCard); // Neu einfügen

                    BaseModel model = App.DataStore.getModelById(card.getId());
                    model.setFlowPanePosition(container.getChildren().indexOf(card));
                    App.DataStore.saveModelAsJSON(model);

                    success = true;
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });

        // Drag abgeschlossen
        card.setOnDragDone(event -> event.consume());
    }

    @FXML
    private void handleOpenSettingsWindow(){
        Stage stage = App.Router.getSettigsStage();
        stage.setScene(App.Router.getSceneSettings());
        stage.show();
    }

    @FXML
    private void openMergeDetailWindow() {
        mergeHelperController.openMergeDetailWindow();
    }

    @FXML
    private void handleMergeButtonClicked(){
        mergeHelperController.handleMergeButtonClicked();
    }


    public void setStageDimensions(Stage stage, MergeDetailController controller) {

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Detail");
        stage.initStyle(StageStyle.DECORATED);
        stage.setMinWidth(App.Hardware.screenWidth()*0.3);
        stage.setMinHeight(App.Hardware.screenHeight()*0.8);

        controller.getStackPane().setMinWidth(stage.getMinWidth());
        controller.getStackPane().setMinHeight(stage.getMinHeight());


    }

    public MergeHelperController getMergeHelperController() {
        return mergeHelperController;
    }


    public ToolBar getToolBar() {
        return toolBar;
    }

    public FlowPane getCardContainer() {
        return cardContainer;
    }
}


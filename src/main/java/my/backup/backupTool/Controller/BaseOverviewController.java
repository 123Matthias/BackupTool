package my.backup.backupTool.Controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import my.backup.backupTool.App;
import my.backup.backupTool.DataRepository.BaseDataStoreRepository;
import my.backup.backupTool.DataRepository.IDataStore;
import my.backup.backupTool.Model.IModel;
import my.backup.backupTool.SceneBuilder;

import java.io.IOException;
import java.util.*;


public class BaseOverviewController {


    @FXML
    private ToolBar toolBar;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private FlowPane flowPane; // Dein FlowPane
    @FXML
    private Pane cardId;
    @FXML
    private VBox cardContainer;


    List<IModel> modelList;

    @FXML
    public void initialize() {

        double toolBarHeight = toolBar.getHeight();
        flowPane.setStyle("-fx-padding: " + (toolBarHeight + 10) + " 0 0 10; -fx-alignment: top-center;");
        modelList = getModelsAsList();
        addAllCardsSorted(modelList);

    }




    public void addNewCard(IModel model) {

        // Randabstand
        Pane newCardPane = new Pane();
        newCardPane.getStyleClass().add("cardPane"); // Stile der Vorlage übernehmen

        //CardContainer
        VBox newCardBox = new VBox();
        Tooltip tooltip = new Tooltip("click for update this card");
        Tooltip.install(newCardBox, tooltip);

        //UUID
        String uid = model.getUid() != null && !model.getUid().isEmpty() ? model.getUid() : "";
        newCardBox.getStyleClass().add("card");
        if(model.getCardWidth() != 0){
            newCardBox.setStyle("-fx-pref-width:" + model.getCardWidth());
        }
        newCardBox.setSpacing(5);
        newCardPane.setId(uid);
        enableDragAndDrop(newCardPane, flowPane);

// Titel (HBox)
        HBox newTitleContainer = new HBox();
        newTitleContainer.getStyleClass().add("cardTitleContainer");

// Titel
        Label titleLabel = new Label();
        titleLabel.setText(model.getTitle());  // Setze den Titel
        titleLabel.getStyleClass().add("cardTitle"); // Stile für den Titel setzen

// Buttons
        Button increaseWidthButton = new Button("+");
        increaseWidthButton.setStyle("-fx-font-size: 16; -fx-padding: 0");
        increaseWidthButton.setOnMouseClicked(event -> {
            double currentWidth = newCardBox.getPrefWidth();
            newCardBox.setPrefWidth(currentWidth + 50);
            newCardBox.setStyle("-fx-pref-width: " + (currentWidth + 50) + "px;");
            model.setCardWidth((int)newCardBox.getPrefWidth());
            App.dataStore.saveModelAsJSON(model);
        });

        Button decreaseWidthButton = new Button("-");
        decreaseWidthButton.setStyle("-fx-font-size: 16; -fx-padding: 0");
        decreaseWidthButton.setOnMouseClicked(event -> {
            double currentWidth = newCardBox.getPrefWidth();
            if (currentWidth > 50) { // Mindestbreite beachten
                newCardBox.setPrefWidth(currentWidth - 50);
                newCardBox.setStyle("-fx-pref-width: " + (currentWidth - 50) + "px;");
                model.setCardWidth((int)newCardBox.getPrefWidth());
                App.dataStore.saveModelAsJSON(model);
            }
        });

// Pane als Platzhalter hinzufügen
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS); // Pane dehnt sich aus, um Platz zu schaffen
        increaseWidthButton.getStyleClass().add("basicButton");
        decreaseWidthButton.getStyleClass().add("basicButton");
// Buttons zur HBox hinzufügen
        newTitleContainer.getChildren().addAll(titleLabel, spacer, increaseWidthButton, decreaseWidthButton);

// CONTENT------------------------------------------------------------
        VBox contentBox = new VBox();
        contentBox.getStyleClass().add("cardContent");
        contentBox.setSpacing(5);
        //UID Label
        Label uuidLabel = new Label("UUID: " + uid);

        contentBox.getChildren().addAll(
                uuidLabel,
                new Label("Last Backup: " + model.getStartDate()),
                new Label("Next Backup: " + model.getNextBackupLocalDateTime()),
                new Label("Source Path: " + model.getSource()),
                new Label("Target Path: " + model.getTarget())
        );

        HBox sourceHashBox = new HBox(10, new Label("Source CRC32: "), new Label() {{
            textProperty().bind(model.getSourceHashProperty());
        }});

        HBox targetHashBox = new HBox(10, new Label("Target CRC32: "), new Label() {{
            textProperty().bind(model.getTargetHashProperty());
        }});


        ProgressBar progressBar = new ProgressBar(0);

        progressBar.progressProperty().bind(model.getProgressStateProperty());

        Label testLabel = new Label();
        testLabel.textProperty().bind(Bindings.format("%.2f", model.getProgressStateProperty()));




        progressBar.prefWidthProperty().bind(contentBox.widthProperty().subtract(10));
        progressBar.translateXProperty().set(5);
        Label progressLabel = new Label();
        progressLabel.setPadding(new Insets(10, 0, 0, 5));
        progressLabel.setLabelFor(progressBar);
        progressLabel.setText("Progress");

        contentBox.setOnMouseClicked(event -> openMergeDetailWindow(uid));


        // Struktur aufbauen
        newCardBox.getChildren().addAll(newTitleContainer, contentBox, sourceHashBox, targetHashBox, progressLabel, progressBar);
        newCardPane.getChildren().add(newCardBox);




        // Neue Karte in das FlowPane einfügen
        flowPane.getChildren().add(newCardPane);  // flowPane muss vorher definiert sein


        System.out.println("...............Card Added ........................");


    }


    private void addAllCardsSorted(List<IModel> list) {
        System.out.println("-----Im in Method addAllCardsSorted-----");
        list.sort(Comparator.comparingInt(IModel::getFlowPanePosition));
        for (IModel entry : list) {
            System.out.println("AddCardFromList getAllCards() " + entry.getTitle());
            addNewCard(entry);
        }
    }

    private List<IModel> getModelsAsList() {
        List<IModel> dataList = new ArrayList<>();
            dataList = App.JobScheduler.getModelList();
            dataList.sort(Comparator.comparingInt(IModel::getFlowPanePosition));


        return dataList;
    }


    @FXML
    public void openMergeDetailWindow(String uid) {
        System.out.println("UID Nummer: " + uid);
        Stage stage = new Stage();
        SceneBuilder newScene = App.Router.createMergeDetail(App.Router.getTheme().toString());

        try {
            stage.setScene(newScene.getScene());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        MergeDetailController controller = newScene.getController();
        controller.openUpdateSceneByUID(uid);
        setStageDimensions(stage, controller);
        stage.show();
    }




    @FXML
    public void openMergeDetailWindow() throws IOException {

        Stage stage = new Stage();
        SceneBuilder sceneBuilder = App.Router.createMergeDetail(App.Router.getTheme().toString());
        stage.setScene(sceneBuilder.getScene());
        MergeDetailController controller = sceneBuilder.getController();
        setStageDimensions(stage, controller);
        stage.show();
    }

    private void setStageDimensions(Stage stage, MergeDetailController controller) {

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UTILITY);
        stage.setMinWidth(600);
        stage.setMaxWidth(600);
        stage.setMinHeight(750);
        stage.setMaxHeight(750);

        controller.getStackPane().setMinWidth(stage.getMinWidth());
        controller.getStackPane().setMaxWidth(stage.getMaxWidth());
        controller.getStackPane().setMinHeight(stage.getMinHeight());
        controller.getStackPane().setMaxHeight(stage.getMaxHeight());

    }




    @FXML
    public void backToMain(){
      //  Main.mainStage.setScene(Main.sceneMain);
        App.Router.getMainStage().setScene(App.Router.getSceneMain());
        App.Router.getMainStage().show();
    }

/*
    @FXML
    private void handleScroll(ScrollEvent event) {
        // Berechne die neue Y-Position der ToolBar
        double deltaY = event.getDeltaY();
        System.out.println(deltaY);
        System.out.println(scrollPane.getVvalue());
        // Wenn das Scrollen am unteren Ende des ScrollPane angekommen ist und deltaY > 0 (nach unten scrollen), nichts weiter tun

        // Wenn das Scrollen am oberen Ende des ScrollPane angekommen ist und deltaY < 0 (nach oben scrollen), nichts weiter tun
        if (scrollPane.getVvalue() == 0.0) {
            return; // Verhindert das Scrollen nach oben
        }
        if (scrollPane.getVvalue() == 0.0 && deltaY < 0) {
            toolBar.setTranslateY(toolBar.getTranslateY() - deltaY);
        }

        if (scrollPane.getVvalue() == 1 && deltaY > 0) {
            toolBar.setTranslateY(toolBar.getTranslateY() - deltaY);
            return;
        }


        if (scrollPane.getVvalue() == 1) {
            return; // Verhindert das Scrollen nach unten
        }


        System.out.println(toolBar.getTranslateY());
        // Passe die Y-Position der ToolBar an
        toolBar.setTranslateY(toolBar.getTranslateY() - deltaY);

        // Verhindern, dass die ToolBar nach oben hinaus verschoben wird
        if (toolBar.getTranslateY() < 0) {
            toolBar.setTranslateY(0);
        }

        // Optional: Wenn die ToolBar zu weit nach unten verschoben wird, setze sie auf 0 zurück
        if (toolBar.getTranslateY() > scrollPane.getHeight()) {
            toolBar.setTranslateY(scrollPane.getHeight());
        }
    }

*/


    private void enableDragAndDrop(Pane card, FlowPane container) {
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

                    IModel model = App.dataStore.getModelById(card.getId());
                    model.setFlowPanePosition(container.getChildren().indexOf(card));
                    App.dataStore.saveModelAsJSON(model);

                    success = true;
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });

        // Drag abgeschlossen
        card.setOnDragDone(event -> event.consume());
    }

}

package my.backup.backupTool.Controller;

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
import my.backup.backupTool.App;
import my.backup.backupTool.JobManagement.BackupJobScheduler;
import my.backup.backupTool.Model.BaseModel;
import my.backup.backupTool.SceneBuilder;

import javax.swing.text.DateFormatter;
import java.io.IOException;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


public abstract class BaseOverviewController {


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



    @FXML
    public void initialize() {

        double toolBarHeight = toolBar.getHeight();
        flowPane.setStyle("-fx-padding: " + (toolBarHeight + 10) + " 0 0 10; -fx-alignment: top-center;");
        addAllCardsSorted(App.DataStore.getModelList());
    }


    public void addNewCard(BaseModel model) {

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
        increaseWidthButton.setStyle("-fx-font-size: 18; -fx-padding: 0");
        increaseWidthButton.setOnMouseClicked(event -> {
            double currentWidth = newCardBox.getPrefWidth();
            newCardBox.setPrefWidth(currentWidth + 50);
            newCardBox.setStyle("-fx-pref-width: " + (currentWidth + 50) + "px;");
            model.setCardWidth((int)newCardBox.getPrefWidth());
            App.DataStore.saveModelAsJSON(model);
        });

        Button decreaseWidthButton = new Button("-");
        decreaseWidthButton.setStyle("-fx-font-size: 18; -fx-padding: 0");
        decreaseWidthButton.setOnMouseClicked(event -> {
            double currentWidth = newCardBox.getPrefWidth();
            if (currentWidth > 50) { // Mindestbreite beachten
                newCardBox.setPrefWidth(currentWidth - 50);
                newCardBox.setStyle("-fx-pref-width: " + (currentWidth - 50) + "px;");
                model.setCardWidth((int)newCardBox.getPrefWidth());
                App.DataStore.saveModelAsJSON(model);
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String lastDate = model.getStartDate() == null ? "no Date" : model.getStartDate().format(formatter);
        String nextDate = model.getNextBackupLocalDateTime() == null ? "no Date" : model.getNextBackupLocalDateTime().format(formatter);

        contentBox.getChildren().addAll(
                uuidLabel,
                new Label("Last Backup: " + lastDate),
                new Label("Next Backup: " + nextDate),
                new Label("Source Path: " + model.getSource()),
                new Label("Target Path: " + model.getTarget())
        );

// Labels direkt erstellen
        Label sourceHashLabel = new Label();
        Label targetHashLabel = new Label();

// HBox mit Labels erstellen
        HBox sourceHashBox = new HBox(10, new Label("Source CRC32: "), sourceHashLabel);
        HBox targetHashBox = new HBox(10, new Label("Target CRC32: "), targetHashLabel);

// Die Text-Eigenschaften binden

        sourceHashLabel.textProperty().bind(model.getSourceHashProperty());
        targetHashLabel.textProperty().bind(model.getTargetHashProperty());


        boolean initialStatus = model.getIsBackupSuccessfullyProperty().get();
        String initialStyle = initialStatus ? "-fx-text-fill: green" : "-fx-text-fill: red";
        sourceHashLabel.setStyle(initialStyle);
        targetHashLabel.setStyle(initialStyle);

// Listener für das Backup-Status
        model.getIsBackupSuccessfullyProperty().addListener((observable, oldValue, newValue) -> {
            // Direkt im Listener die Farbe setzen
            String style = newValue ? "-fx-text-fill: green" : "-fx-text-fill: red";
            sourceHashLabel.setStyle(style);
            targetHashLabel.setStyle(style);
        });

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

        contentBox.setOnMouseClicked(event -> openDetailWindow(uid));

        // Struktur aufbauen
        newCardBox.getChildren().addAll(newTitleContainer, contentBox, sourceHashBox, targetHashBox, progressLabel, progressBar);
        newCardPane.getChildren().add(newCardBox);

        // Neue Karte in das FlowPane einfügen
        flowPane.getChildren().add(newCardPane);  // flowPane muss vorher definiert sein


        System.out.println("...............Card Added ........................");

    }

    private void addAllCardsSorted(List<BaseModel> list) {
        System.out.println("-----Im in Method addAllCardsSorted-----");
        list.sort(Comparator.comparingInt(BaseModel::getFlowPanePosition));
        for (BaseModel entry : list) {
            System.out.println("AddCardFromList getAllCards() " + entry.getTitle());
            addNewCard(entry);
        }
    }

    private List<BaseModel> getModelsAsList() {
        List<BaseModel> dataList = new ArrayList<>();
            dataList = App.DataStore.getModelList();
            dataList.sort(Comparator.comparingInt(BaseModel::getFlowPanePosition));


        return dataList;
    }


    public void openDetailWindow(String uid) {};
    public void openDetailWindow() {};



    @FXML
    public void backToMain(){
      //  Main.mainStage.setScene(Main.sceneMain);
        App.Router.getMainStage().setScene(App.Router.getSceneMain());
        App.Router.getMainStage().show();
    }


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

}

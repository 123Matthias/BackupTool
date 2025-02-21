package my.backup.backupTool.Controller.Merge;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import my.backup.backupTool.App;
import my.backup.backupTool.Controller.MainController;
import my.backup.backupTool.Model.BaseModel;
import my.backup.backupTool.SceneBuilder;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class MergeHelperController {

    private final MainController mainController;

    public MergeHelperController(MainController mainController) {
        this.mainController = mainController;
    }

    public void openMergeDetailWindow(String uid) {
        //First get the Model!! then do the rest. NullPointer
        BaseModel model = App.DataStore.getModelById(uid);
        System.out.println("UID Nummer: " + uid);
        Stage stage = new Stage();
        SceneBuilder newScene = App.Router.createMergeDetail(App.Router.getTheme().toString());

        try {
            stage.setScene(newScene.getScene());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        MergeDetailController controller = newScene.getController();
        controller.openUpdateScene(model);
        mainController.setStageDimensions(stage, controller);
        stage.show();
    }

    public void openMergeDetailWindow() {

        Stage stage = new Stage();
        SceneBuilder sceneBuilder = App.Router.createMergeDetail(App.Router.getTheme().toString());
        try {
            stage.setScene(sceneBuilder.getScene());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        MergeDetailController controller = sceneBuilder.getController();
        mainController.setStageDimensions(stage, controller);
        stage.centerOnScreen();
        stage.show();
    }

    public void addAllCardsSorted(List<BaseModel> list, boolean isDraggable) {
        System.out.println("-----Im in Method addAllCardsSorted-----");
        list.sort(Comparator.comparingInt(BaseModel::getFlowPanePosition));
        for (BaseModel entry : list) {
            System.out.println("AddCardFromList getAllCards() " + entry.getTitle());
            addNewCard(entry, true);
        }
    }

    public void handleMergeButtonClicked() {
        mainController.getCardContainer().getChildren().clear();
        addAllCardsSorted(App.DataStore.getModelList(), true);

    }

    public void addNewCard(BaseModel model, boolean isDraggable) {

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
        newCardBox.getStyleClass().add("cardCol");
        if(model.getCardWidth() != 0){
            newCardBox.setStyle("-fx-pref-width:" + model.getCardWidth());
        }
        newCardBox.setSpacing(5);
        //Required for drag and drop feature
        newCardPane.setId(uid);

// Titel (HBox)
        HBox newTitleContainer = new HBox();
        newTitleContainer.getStyleClass().add("cardTitleContainer");

// Titel
        Label titleLabel = new Label();
        titleLabel.setText(model.getTitle());  // Setze den Titel
        titleLabel.getStyleClass().add("cardTitle"); // Stile für den Titel setzen

// Buttons
        Button increaseWidthButton = new Button("»");
        increaseWidthButton.setOnMouseClicked(event -> {
            double currentWidth = newCardBox.getPrefWidth();
            newCardBox.setPrefWidth(currentWidth + 50);
            newCardBox.setStyle("-fx-pref-width: " + (currentWidth + 50) + "px;");
            model.setCardWidth((int)newCardBox.getPrefWidth());
            App.DataStore.updateModelInList(model);
        });

        Button decreaseWidthButton = new Button("«");
        decreaseWidthButton.setOnMouseClicked(event -> {
            double currentWidth = newCardBox.getPrefWidth();
            if (currentWidth > 50) { // Mindestbreite beachten
                newCardBox.setPrefWidth(currentWidth - 50);
                newCardBox.setStyle("-fx-pref-width: " + (currentWidth - 50) + "px;");
                model.setCardWidth((int)newCardBox.getPrefWidth());
                App.DataStore.updateModelInList(model);
            }
        });

        String starredString = model.isStarred() ? "★" : "☆";
        Button starredButton = new Button(starredString);
        starredButton.setOnMouseClicked(event -> {
            if(model.isStarred()){
                starredButton.setText("☆");
                model.setStarred(false);

            }
            else{
                starredButton.setText("★");
                model.setStarred(true);

            }
            App.DataStore.updateModelInList(model);
        });



// Pane als Platzhalter hinzufügen
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS); // Pane dehnt sich aus, um Platz zu schaffen
        increaseWidthButton.getStyleClass().add("basicButtonCard");
        decreaseWidthButton.getStyleClass().add("basicButtonCard");
        starredButton.getStyleClass().add("basicButtonCard");
// Buttons zur HBox hinzufügen
        newTitleContainer.getChildren().addAll(titleLabel, spacer, starredButton, decreaseWidthButton, increaseWidthButton);

// CONTENT------------------------------------------------------------
        VBox contentBox = new VBox();
        contentBox.getStyleClass().add("cardContent");
        contentBox.setSpacing(5);

        Label lastDateLabel = new Label();
        lastDateLabel.textProperty().bind(model.TransientProperties.getLastBackupTimeProperty());

        Label nextDateLabel = new Label();
        nextDateLabel.textProperty().bind(model.TransientProperties.getNextBackupTimeProperty());

// Inhalte hinzufügen
        contentBox.getChildren().addAll(
                createLabeledRow("UUID:", uid),
                createLabeledRow("Last Backup:", lastDateLabel),
                createLabeledRow("Next Backup:", nextDateLabel),
                createLabeledRow("Source Path:", model.getSource()));

        HBox row;
        HBox rowEncType;
        if(model.getCheckBoxEncryptionJob()){
            row = createLabeledRow("Target Path:", model.getTarget() + " 🔒");
            rowEncType = createLabeledRow("Encryprion:", model.getEncryptionType().toString());
            contentBox.getChildren().addAll(row,rowEncType);
        }
        else {
            row = createLabeledRow("Target Path:", model.getTarget());
            contentBox.getChildren().addAll(row);
        }

        HBox sourceValidation;
        HBox targetValidation;
        HBox validationType;
        if(model.getCheckBoxValidationJob()){
            // Labels direkt erstellen
            Label sourceHashLabel = new Label();
            Label targetHashLabel = new Label();
            sourceHashLabel.textProperty().bind(model.TransientProperties.getSourceValidationProperty());
            targetHashLabel.textProperty().bind(model.TransientProperties.getTargetValidationProperty());

            // HBox mit Labels erstellen (Label direkt übergeben!)
            sourceValidation = createLabeledRow("Valid Files: ", sourceHashLabel);
            targetValidation = createLabeledRow("Total Files: ", targetHashLabel);
            validationType = createLabeledRow("Validation: ", model.getValidationType().toString());

            boolean initialStatus;
            String value = model.getSourceValidationValue();
            if(value != null && value.equals(model.getTargetValidationValue())){
                initialStatus = true;
            }
            else {
                initialStatus = false;
            }
            String initialStyle = initialStatus ? "-fx-text-fill: green" : "-fx-text-fill: red";
            sourceHashLabel.setStyle(initialStyle);
            targetHashLabel.setStyle(initialStyle);


            // Add Content to Card
            contentBox.getChildren().addAll(sourceValidation,targetValidation, validationType);
        }
        else {
            sourceValidation = createLabeledRow("","");
            targetValidation = createLabeledRow("","");

            // Add Content to Card spacing null row
            contentBox.getChildren().addAll(sourceValidation,targetValidation);
        }

        //Progress Bar
        ProgressBar progressBar = new ProgressBar(0);
        progressBar.progressProperty().bind(model.TransientProperties.getProgressStateProperty());
        progressBar.prefWidthProperty().bind(contentBox.widthProperty());
        Label progressLabel = new Label();
        progressLabel.setPadding(new Insets(10, 0, 0, 0));
        progressLabel.setLabelFor(progressBar);
        progressLabel.textProperty().bind(Bindings.format("Working speed: %.2f MB/sec", model.TransientProperties.getWorkingSpeedProperty()));

        //Click Listener
        contentBox.setOnMouseClicked(event -> openMergeDetailWindow(uid));

        // Struktur aufbauen
        newCardBox.getChildren().addAll(newTitleContainer, contentBox, progressLabel, progressBar);
        newCardBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
        newCardPane.getChildren().add(newCardBox);

        // Neue Karte in das FlowPane einfügen
        mainController.getCardContainer().getChildren().add(newCardPane);  // flowPane muss vorher definiert sein

        //DragAndDrop
        if(isDraggable){
            mainController.enableDragAndDrop(newCardPane, mainController.getCardContainer());
        }


        System.out.println("...............Card Added ........................");
    }

    private HBox createLabeledRow(String labelText, String valueText) {
        Label label = new Label(labelText);
        label.setMinWidth(80); // Feste Breite für alle Labels
        label.setAlignment(Pos.CENTER_LEFT);

        Label valueLabel = new Label(valueText);

        HBox row = new HBox(label, valueLabel);
        row.setSpacing(5);
        return row;
    }

    private HBox createLabeledRow(String labelText, Label valueLabel) {
        Label label = new Label(labelText);
        label.setMinWidth(80); // Feste Breite für alle Labels
        label.setAlignment(Pos.CENTER_LEFT);

        HBox row = new HBox(label, valueLabel);
        row.setSpacing(5);
        return row;
    }

}

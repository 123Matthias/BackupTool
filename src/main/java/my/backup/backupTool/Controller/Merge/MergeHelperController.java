package my.backup.backupTool.Controller.Merge;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import my.backup.backupTool.App;
import my.backup.backupTool.Controller.MainController;
import my.backup.backupTool.Model.BaseModel;
import my.backup.backupTool.SceneBuilder;

import javax.swing.text.html.ImageView;
import java.awt.*;
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

    public void handleMergeOverviewButtonClicked() {
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

        HBox validFilesRow;
        HBox totalFilesRow;
        HBox validationType;
        if(model.getCheckBoxValidationJob()){
            // Labels direkt erstellen
            Label validFilesCount = new Label();
            Label totalFilesCount = new Label();
            validFilesCount.textProperty().bind(model.TransientProperties.getValidFilesCountProperty());
            totalFilesCount.textProperty().bind(model.TransientProperties.getTotalFilesCountProperty());


            BooleanBinding valuesEqual = model.TransientProperties.getValidFilesCountProperty()
                    .isEqualTo(model.TransientProperties.getTotalFilesCountProperty());

            validFilesCount.textFillProperty().bind(
                    Bindings.when(valuesEqual).then(Color.GREEN).otherwise(Color.RED)
            );

            // HBox mit Labels erstellen (Label direkt übergeben!)
            validFilesRow = createLabeledRow("Valid Files: ", validFilesCount);
            totalFilesRow = createLabeledRow("Total Files: ", totalFilesCount);
            validationType = createLabeledRow("Validation: ", model.getValidationType().toString());

            boolean initialStatus;
            int value = model.getValidFilesCount();
            if(value != 0 && value == model.getTotalVisitedFiles()){
                initialStatus = true;
            }
            else {
                initialStatus = false;
            }
            String initialStyle = initialStatus ? "-fx-text-fill: green" : "-fx-text-fill: red";
            validFilesCount.setStyle(initialStyle);
            totalFilesCount.setStyle("-fx-text-fill: -fx-color1");


            // Add Content to Card
            contentBox.getChildren().addAll(validFilesRow,totalFilesRow, validationType);
        }
        else {
            validFilesRow = createLabeledRow("","");
            totalFilesRow = createLabeledRow("","");

            // Add Content to Card spacing null row
            contentBox.getChildren().addAll(validFilesRow,totalFilesRow);
        }

//Progress bottom card Start--------------------------------------
// Progress Bar
        ProgressBar progressBar = new ProgressBar(0);
        progressBar.progressProperty().bind(model.TransientProperties.getProgressStateProperty());
        progressBar.prefWidthProperty().bind(contentBox.widthProperty());

// Label Progress Speed
        Label progressLabel = new Label();
        progressLabel.setPadding(new Insets(0, 5, 0, 0));
        progressLabel.setStyle("-fx-font-style: italic");
        progressLabel.setLabelFor(progressBar);
        progressLabel.textProperty().bind(Bindings.format("%.2f MB / sec", model.TransientProperties.getWorkingSpeedProperty()));


// HBox for float right
        HBox progressLabelBox = new HBox();
        progressLabelBox.setAlignment(Pos.CENTER_RIGHT); // Label rechtsbündig
        HBox.setHgrow(progressLabelBox, Priority.ALWAYS); // Label dehnt sich auf volle Breite aus

        // SVGPath erstellen und Inhalt setzen
        SVGPath svgPath = new SVGPath();
        if(model.hasBackupJob() && model.getNextBackupLocalDateTime() != null){
            svgPath.setContent("M73 39c-14.8-9.1-33.4-9.4-48.5-.9S0 62.6 0 80L0 432c0 17.4 9.4 33.4 24.5 41.9s33.7 8.1 48.5-.9L361 297c14.3-8.7 23-24.2 23-41s-8.7-32.2-23-41L73 39z");
            svgPath.setStyle("-fx-stroke: -fx-playColor; -fx-fill: -fx-playColor;");
        }
        else{
            svgPath.setContent("M464 256A208 208 0 1 0 48 256a208 208 0 1 0 416 0zM0 256a256 256 0 1 1 512 0A256 256 0 1 1 0 256zm224-72l0 144c0 13.3-10.7 24-24 24s-24-10.7-24-24l0-144c0-13.3 10.7-24 24-24s24 10.7 24 24zm112 0l0 144c0 13.3-10.7 24-24 24s-24-10.7-24-24l0-144c0-13.3 10.7-24 24-24s24 10.7 24 24z");
            svgPath.setStyle("-fx-stroke: -fx-pauseColor; -fx-fill: -fx-pauseColor;");
        }

        svgPath.setScaleX(0.06);
        svgPath.setScaleY(0.06);

// Label als Container für das SVG erstellen
        Label svgLabel = new Label();
        svgLabel.setGraphic(svgPath);

        svgLabel.setMinSize(30, 30);
        svgLabel.setMaxSize(30, 30);
        svgLabel.setPadding(new Insets(0, 20, 0, 0));
        svgLabel.setAlignment(Pos.CENTER);  // Zentriert das SVG in Label
        progressLabelBox.getChildren().addAll(svgLabel,progressLabel);

// Struktur aufbauen
        newCardBox.getChildren().addAll(newTitleContainer, contentBox, progressLabelBox, progressBar);
        newCardBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
        newCardPane.getChildren().add(newCardBox);
//Progress bottom card End--------------------------------------
        //Click Listener
        contentBox.setOnMouseClicked(event -> openMergeDetailWindow(uid));
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
        label.setMinWidth(100); // Feste Breite für alle Labels
        label.setAlignment(Pos.CENTER_LEFT);

        Label valueLabel = new Label(valueText);

        HBox row = new HBox(label, valueLabel);
        row.setSpacing(5);
        return row;
    }

    private HBox createLabeledRow(String labelText, Label valueLabel) {
        Label label = new Label(labelText);
        label.setMinWidth(100); // Feste Breite für alle Labels
        label.setAlignment(Pos.CENTER_LEFT);

        HBox row = new HBox(label, valueLabel);
        row.setSpacing(5);
        return row;
    }

}

package my.backup.backupTool.Controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import my.backup.backupTool.App;
import my.backup.backupTool.DataRepository.ILoadData;
import my.backup.backupTool.DataRepository.BaseDataRepository;
import my.backup.backupTool.Model.IModel;
import my.backup.backupTool.SceneBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


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
    private void initialize() {
        // Listener für Änderungen an der Höhe der ToolBar
        toolBar.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                // Sobald die Höhe der ToolBar sich ändert, das Padding des FlowPane anpassen
                double toolBarHeight = toolBar.getHeight();
                flowPane.setStyle("-fx-padding: " + (toolBarHeight + 10) + " 0 0 10; -fx-alignment: top-center;");
            }
        });

        getAllCards();
    }


    @FXML
    public void addNewCard(IModel model) {
        // Randabstand
        Pane newCard = new Pane();
        newCard.getStyleClass().add("cardPane"); // Stile der Vorlage übernehmen

        //CardContainer
        VBox newContent = new VBox();

        Tooltip tooltip = new Tooltip("click for update this card");
        Tooltip.install(newContent, tooltip);

        //UUID
        String uid = model.getUid() != null && !model.getUid().isEmpty() ? model.getUid() : "";
        newContent.setId(uid);
        newContent.getStyleClass().add("card");
        newContent.setSpacing(5);
        newContent.setOnMouseClicked(event -> openMergeDetailWindow(uid));

        // Titel (HBox)
        HBox newTitleContainer = new HBox();
        newTitleContainer.getStyleClass().add("cardTitleContainer");

        // Titel
        Label titleLabel = new Label();
        titleLabel.setText(model.getTitle());  // Setze den Titel
        titleLabel.getStyleClass().add("cardTitle"); // Stile für den Titel setzen
        newTitleContainer.getChildren().add(titleLabel);

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

        // Struktur aufbauen
        newContent.getChildren().addAll(newTitleContainer, contentBox);
        newCard.getChildren().add(newContent);

        // Neue Karte in das FlowPane einfügen
        flowPane.getChildren().add(newCard);  // flowPane muss vorher definiert sein
        System.out.println("...............Card Added ........................");
    }


    @FXML
    private void getAllCards() {
        ILoadData data = new BaseDataRepository();
        List<IModel> dataList = new ArrayList<>();
        try {
            dataList = data.getAllAsList();

            // Prüfen, ob die Liste leer ist
            if (dataList.isEmpty()) {
                System.out.println("Die Liste ist leer. Vorgang wird abgebrochen.");
                return; // Methode abbrechen, wenn die Liste leer ist
            }

            // Liste iterieren und Karten hinzufügen
            for (IModel entry : dataList) {
                System.out.println("addCard: " + entry.getTitle());
                addNewCard(entry);
            }
        } catch (IOException e) {
            // Fehler behandeln und eine klare Nachricht ausgeben
            System.err.println("Fehler beim Laden der Daten: " + e.getMessage());
            throw new RuntimeException(e);
        }
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

}

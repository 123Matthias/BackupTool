package my.backup.backupTool.Controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import my.backup.backupTool.Data.ILoadData;
import my.backup.backupTool.Data.MergeData;
import my.backup.backupTool.Main;
import my.backup.backupTool.Model.IModel;

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
                flowPane.setStyle("-fx-padding: " + (toolBarHeight + 10) + " 0 0 0;");
            }
        });


    }


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



    @FXML
    public void addNewCard(IModel model) {
        // Neue Karte erstellen
        Pane newCard = new Pane();
        newCard.getStyleClass().addAll(cardId.getStyleClass()); // Stile der Vorlage übernehmen
        newCard.setVisible(true); // Sichtbar machen

        // Hauptcontainer (VBox) klonen
        VBox contentTemplate = (VBox) cardId.lookup("#fluentContainer"); // Original-Container
        VBox newContent = new VBox();
        newContent.getStyleClass().addAll(contentTemplate.getStyleClass());
        newContent.setSpacing(5);

        // Titelcontainer (HBox) klonen
        HBox titleTemplate = (HBox) contentTemplate.lookup(".cardTitleContainer"); // Original-Titel
        HBox newTitleContainer = new HBox();
        newTitleContainer.getStyleClass().addAll(titleTemplate.getStyleClass());

        Label titleLabel = new Label("My Custom Title"); // Titel setzen
        titleLabel.getStyleClass().add("cardTitle");
        newTitleContainer.getChildren().add(titleLabel);

        // Inhalte (Labels) setzen
        VBox contentBox = new VBox();
        contentBox.getStyleClass().add("cardContent");
        contentBox.setSpacing(5);
        contentBox.getChildren().addAll(
                new Label("Last Backup: Test1"),
                new Label("Next Backup: Test2"),
                new Label("Source Path: Test3"),
                new Label("Target Path: Test4")
        );

        // Struktur aufbauen
        newContent.getChildren().addAll(newTitleContainer, contentBox);
        newCard.getChildren().add(newContent);

        // Neue Karte in das FlowPane einfügen
        flowPane.getChildren().add(newCard);
    }

    public void getAllCards(){
        ILoadData data = new MergeData();
        List<IModel> dataList = new ArrayList<>();
        try {
            data.getAllAsList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (IModel entry: dataList) {
            addNewCard(entry);
        }

    }

    @FXML
    public void openMergeDetailWindow(){
        Stage stage = new Stage();
        stage.setScene(Main.sceneMerge);
        stage.show();
    }

    @FXML
    public void backToMain(){
        Main.mainStage.setScene(Main.sceneMain);
        Main.mainStage.show();
    }


}

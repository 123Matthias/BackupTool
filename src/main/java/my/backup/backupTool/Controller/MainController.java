package my.backup.backupTool.Controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import my.backup.backupTool.App;
import my.backup.backupTool.Controller.Merge.MergeDetailController;
import my.backup.backupTool.Controller.Merge.MergeHelperController;
import my.backup.backupTool.Enumerations.LeftToolbar;
import my.backup.backupTool.Enumerations.MessageTYPE;
import my.backup.backupTool.Notifications.MessageService;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class MainController extends BaseMainController {

    @FXML
    private ToolBar topToolbar;

    @FXML
    private FlowPane cardContainer;



    private final MergeHelperController mergeHelperController;
    private final StarredHelperController starredHelperController;


    public MainController() {
        mergeHelperController = new MergeHelperController(this);
        starredHelperController = new StarredHelperController(this);
    }

    @FXML
    public void initialize(){
        mergeHelperController.addAllCardsSorted(App.DataStore.getModelList(),true);
    }


    @FXML
    public void backToMain(){
        //  Main.mainStage.setScene(Main.sceneMain);
        App.Router.getMainStage().setScene(App.Router.getMainScene());
        App.Router.getMainStage().show();
    }


    @FXML
    private void handleSettingsButtonClicked(){
        super.setLeftToolbarSelection(LeftToolbar.SettingsButton);
        SettingsController controller = App.Router.getSettingsController();
        controller.createHWInfoRows();
        Stage stage = App.Router.getSettigsStage();
        stage.setScene(App.Router.getSettingsScene());
        stage.show();
        App.SettingsDataStore.createSaveOnCloseSettingsWindowListener();
    }

    @FXML
    private void openMergeDetailWindow() {
        mergeHelperController.openMergeDetailWindow();
    }

    @FXML
    private void handleMergeButtonClicked(){
        super.setLeftToolbarSelection(LeftToolbar.MergeButton);
        Platform.runLater(mergeHelperController::handleMergeOverviewButtonClicked);
    }

    @FXML
    private void handleStarredButtonClick(){
        super.setLeftToolbarSelection(LeftToolbar.StarredButton);
        Platform.runLater(this.starredHelperController::handleStarredButtonClick);
    }

    @FXML
    private void handleBackupLogButtonClicked(){
        super.setLeftToolbarSelection(LeftToolbar.LogButton);
        String logFilePath = App.Properties.getLogFilePath(); // Datei-Pfad holen
        if (logFilePath == null || logFilePath.isEmpty()) {
            System.out.println("Log-Dateipfad nicht gesetzt.");
            return;
        }

        File logFile = new File(logFilePath);
        if (!logFile.exists()) {
            System.out.println("Log-Datei existiert nicht: " + logFilePath);
            return;
        }

        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(logFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            MessageService.createToast("not Supported", MessageTYPE.DANGER);
        }
    }

    @FXML
    private void handleValidationLogButtonClicked(){
        super.setLeftToolbarSelection(LeftToolbar.ValidationLogButton);
        String logFilePath = App.Properties.getValidationLogFilePath(); // Datei-Pfad holen
        if (logFilePath == null || logFilePath.isEmpty()) {
            System.out.println("Log-Dateipfad nicht gesetzt.");
            return;
        }

        File logFile = new File(logFilePath);
        if (!logFile.exists()) {
            System.out.println("Log-Datei existiert nicht: " + logFilePath);
            return;
        }

        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(logFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            MessageService.createToast("not Supported", MessageTYPE.DANGER);
        }
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
        return topToolbar;
    }

    public FlowPane getCardContainer() {
        return cardContainer;
    }

    @FXML
    public void saveAll(){
        if(App.DataStore.saveModelListAsJSON()){
            MessageService.createToast("Saved All", MessageTYPE.SAVE);
        }
    }



}

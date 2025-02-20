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
        super.setLeftToolbarSelection(LeftToolbar.MergeButton);
        Platform.runLater(mergeHelperController::handleMergeButtonClicked);
    }

    @FXML
    private void handleStarredButtonClick(){
        super.setLeftToolbarSelection(LeftToolbar.StarredButton);
        Platform.runLater(this.starredHelperController::handleStarredButtonClick);
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



}

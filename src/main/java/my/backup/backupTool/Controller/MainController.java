package my.backup.backupTool.Controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import my.backup.backupTool.App;
import my.backup.backupTool.Controller.Merge.MergeDetailController;
import my.backup.backupTool.Controller.Merge.MergeHelperController;
import my.backup.backupTool.Model.BaseModel;

import javax.tools.Tool;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MainController extends BaseMainController {

    @FXML
    private ToolBar topToolbar;

    @FXML
    private FlowPane cardContainer;

    @FXML
    private ToolBar leftToolbar;

    private final MergeHelperController mergeHelperController;
    private final StarredHelperController starredHelperController;


    public MainController() {
        mergeHelperController = new MergeHelperController(this);
        starredHelperController = new StarredHelperController(this);
    }

    @FXML
    public void initialize(){
        mergeHelperController.addAllCardsSorted(App.DataStore.getModelList());
    }


    @FXML
    public void backToMain(){
        //  Main.mainStage.setScene(Main.sceneMain);
        App.Router.getMainStage().setScene(App.Router.getMainScene());
        App.Router.getMainStage().show();
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

    @FXML
    private void handleStarredButtonClick(){
        this.starredHelperController.handleStarredButtonClick();

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

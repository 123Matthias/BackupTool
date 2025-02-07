package my.backup.backupTool.Controller;

import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import my.backup.backupTool.App;
import my.backup.backupTool.Model.BaseModel;
import my.backup.backupTool.SceneBuilder;

import java.io.IOException;

public class MergeOverviewController extends BaseOverviewController {



    public MergeOverviewController() {

    }

    @FXML
    public void initialize(){
        super.initialize();

    }

    @Override
    @FXML
    public void openDetailWindow(String uid) {
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
        setStageDimensions(stage, controller);
        stage.show();
    }

    @Override
    @FXML
    public void openDetailWindow() {

        Stage stage = new Stage();
        SceneBuilder sceneBuilder = App.Router.createMergeDetail(App.Router.getTheme().toString());
        try {
            stage.setScene(sceneBuilder.getScene());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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


}

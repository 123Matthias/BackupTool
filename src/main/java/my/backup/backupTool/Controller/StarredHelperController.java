package my.backup.backupTool.Controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import my.backup.backupTool.App;
import my.backup.backupTool.Controller.Merge.MergeHelperController;
import my.backup.backupTool.Model.BaseModel;

import java.util.ArrayList;
import java.util.List;

public class StarredHelperController {

    MainController mainController;

    public StarredHelperController(MainController mainController) {
        this.mainController = mainController;
    }

    public void handleStarredButtonClick(){

        mainController.getCardContainer().getChildren().clear();
        for(BaseModel baseModel : App.DataStore.getModelList()) {
            if(baseModel.isStarred()){
                mainController.getMergeHelperController().addNewCard(baseModel);
            }
        }

    }

}

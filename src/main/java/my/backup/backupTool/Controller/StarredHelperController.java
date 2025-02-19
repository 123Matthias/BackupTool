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
    List<BaseModel> starredlist;

    public StarredHelperController(MainController mainController) {
        this.mainController = mainController;
        this.starredlist = new ArrayList<>();
    }

    public void handleStarredButtonClick(){
        starredlist.clear();
        mainController.getCardContainer().getChildren().clear();
        for(BaseModel baseModel : App.DataStore.getModelList()) {
            if(baseModel.isStarred()) {
                this.starredlist.add(baseModel);
            }
        }
        mainController.getMergeHelperController().addAllCardsSorted(starredlist,true);

    }

}

package my.backup.backupTool.Model;


import my.backup.backupTool.Notifications.IMessageList;
import my.backup.backupTool.Notifications.MessageList;

public class MergeModel extends BaseModel {

    IMessageList messages;

    public MergeModel() {
        this.messages = new MessageList();
    }
}

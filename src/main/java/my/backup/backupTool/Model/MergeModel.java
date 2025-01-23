package my.backup.backupTool.Model;


import my.backup.backupTool.Service.IMessageList;
import my.backup.backupTool.Service.MessageList;

public class MergeModel extends BaseModel {

    IMessageList messages;

    public MergeModel() {
        this.messages = new MessageList();
    }
}

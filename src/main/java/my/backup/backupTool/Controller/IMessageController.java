package my.backup.backupTool.Controller;

import my.backup.backupTool.MessageTYPE;
import my.backup.backupTool.Service.IMessageList;

public interface IMessageController {

    void show(IMessageList message, MessageTYPE messageType);
}

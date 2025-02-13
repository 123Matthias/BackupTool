package my.backup.backupTool.Notifications;

import java.util.ArrayList;

public interface IMessageList {
    ArrayList<String> getMessagesAsList();
    void addMessage(String message);
}

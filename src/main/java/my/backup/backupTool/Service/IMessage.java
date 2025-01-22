package my.backup.backupTool.Service;

import java.util.ArrayList;

public interface IMessage {

    ArrayList<String> getMessagesAsList();
    void addMessage(String message);
}

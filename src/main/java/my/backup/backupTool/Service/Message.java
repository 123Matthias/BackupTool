package my.backup.backupTool.Service;

import java.util.ArrayList;

public class Message implements IMessage {
    private final ArrayList<String> messages;

    public Message() {
        messages = new ArrayList<>();
    }

    public ArrayList<String> getMessagesAsList() {
        return messages;
    }

    public void addMessage(String message) {
        this.messages.add(message);
    }
}

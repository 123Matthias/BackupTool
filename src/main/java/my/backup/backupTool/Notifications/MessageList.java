package my.backup.backupTool.Notifications;

import java.util.ArrayList;

public class MessageList implements IMessageList {
    private final ArrayList<String> messages;

    public MessageList() {
        messages = new ArrayList<>();
    }

    @Override
    public ArrayList<String> getMessagesAsList() {
        return messages;
    }

    @Override
    public void addMessage(String message) {
        this.messages.add(message);
    }
}

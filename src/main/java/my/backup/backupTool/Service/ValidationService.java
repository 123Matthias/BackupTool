package my.backup.backupTool.Service;

import my.backup.backupTool.Controller.MessageController;

import java.io.File;
import java.io.IOException;

public class ValidationService {

    public static void validatePath(File sourceDir, File targetDir) {

        Message messages = new Message();
        String sourceDisk = sourceDir.toString().substring(0, 2);
        String targetDisk = targetDir.toString().substring(0, 2);

        if (!sourceDir.exists() || !targetDir.exists()) {
            System.out.println("Source: " + sourceDir);
            System.out.println("Target" + targetDir);
            System.out.println("Quell- oder Zielordner existieren nicht.");
            messages.addMessage("Quell- oder Zielordner existieren nicht.");
        }


        System.out.println("sourceDisk: " + sourceDisk + " targetDisk: " + targetDisk);
        if (sourceDisk.equalsIgnoreCase(targetDisk)) {
            messages.addMessage("Source and Target Disk are the same: " + sourceDisk + " and " + targetDisk);

        }

        MessageController messageController = new MessageController();
        messageController.setMessage(messages);

    }
}


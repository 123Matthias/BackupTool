package my.backup.backupTool.Factory;

import my.backup.backupTool.Encryption.AesService;
import my.backup.backupTool.Model.BaseModel;
import my.backup.backupTool.Encryption.EncryptionTYPE;
import my.backup.backupTool.CopyServices.BaseCopyService;
import my.backup.backupTool.CopyServices.ICopyService;

public class CopyServiceFactory {
     public static ICopyService createCopyService(BaseModel model) {
         System.out.println("Encryption Type AES CBC: " + model.getEncryptionType());
            if (model.getEncryptionType() == EncryptionTYPE.AES_CBC) {
                return new AesService(model); // Verschlüsselter Service

            } else {
                return new BaseCopyService(model); // Normaler Kopierservice
            }
        }
}

package my.backup.backupTool.Factory;

import my.backup.backupTool.ServiceEncryption.AESCTRService;
import my.backup.backupTool.ServiceEncryption.AesGCMService;
import my.backup.backupTool.Model.BaseModel;
import my.backup.backupTool.Enumerations.EncryptionTYPE;
import my.backup.backupTool.Services.BaseCopyService;
import my.backup.backupTool.Services.ICopyService;

public class CopyServiceFactory {
     public static ICopyService createCopyService(BaseModel model) {
         System.out.println("Encryption Type AES CBC: " + model.getEncryptionType());
            if (model.getEncryptionType() == EncryptionTYPE.AES_GCM) {
                return new AesGCMService(model); // Verschlüsselter Service

            } else if (model.getEncryptionType() == EncryptionTYPE.AES_CTR) {
                return new AESCTRService(model);
                
            } else {
                return new BaseCopyService(model); // Normaler Kopierservice
            }
        }
}

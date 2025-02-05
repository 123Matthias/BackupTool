package my.backup.backupTool.Factory;

import my.backup.backupTool.Encryption.AesService;
import my.backup.backupTool.Model.BaseModel;
import my.backup.backupTool.Model.EncryptionTYPE;
import my.backup.backupTool.Service.BaseCopyService;
import my.backup.backupTool.Service.ICopyService;

public class CopyServiceFactory {
     public static ICopyService createCopyService(BaseModel model) {
            if (model.getEncryptionType() == EncryptionTYPE.AES_CBC) {
                return new AesService(model); // Verschlüsselter Service
            } else {
                return new BaseCopyService(model); // Normaler Kopierservice
            }
        }
}

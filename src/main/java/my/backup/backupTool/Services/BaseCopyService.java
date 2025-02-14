package my.backup.backupTool.Services;

import my.backup.backupTool.Model.BaseModel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;

public class BaseCopyService extends BaseCalculationService implements ICopyService {

    private final BaseModel model;

    public BaseCopyService(BaseModel model) {
        this.model = model;
    }

    //do not USE this Method. very slow by copying to external Disks.
    protected void copyFileWithStream(Path sourceFile, Path targetFile, long totalSize) {

        try (InputStream in = Files.newInputStream(sourceFile);
             OutputStream out = Files.newOutputStream(targetFile,
                     StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {

            byte[] buffer = new byte[8192];
            int bytesRead;
            long fileProcessedSize = 0;

            //END OF Encryption Service usage
            while ((bytesRead = in.read(buffer)) != -1) {

                out.write(buffer, 0, bytesRead);
                fileProcessedSize += bytesRead;

                // Fortschritt berechnen
                long progress = fileProcessedSize / totalSize;
                super.updateProgressBar(this.model);

             //   super.calculateWorkingSpeed(fileProcessedSize,this.model);
            }

        } catch(IOException e) {
            super.messageList.addMessage(e.getMessage());
        }
    }

    public void copyFileWithFileChannelTT(Path sourceFile, Path targetFile, long totalSize) {
        super.setLastProcessedSize(0);
        try (FileChannel sourceChannel = FileChannel.open(sourceFile, StandardOpenOption.READ);
             FileChannel targetChannel = FileChannel.open(targetFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {

            long fileProcessedSize = 0;
            long transferBufferSize = super.DEFAULT_BUFFERSIZE; // 64kiB Buffer initial
            long fileSize = sourceChannel.size();
            //If File size greater than 1MB
               transferBufferSize = super.calculateBufferSize(fileSize);
            //   System.out.println("transferBufferSize: " + transferBufferSize + " fileSize: " + fileSize);

            while (fileProcessedSize < fileSize) {

                long bytesTransferred = sourceChannel.transferTo(fileProcessedSize, transferBufferSize, targetChannel);
                fileProcessedSize += bytesTransferred;
                super.addFileProcessedSize(bytesTransferred);
                super.updateProgressBar(this.model);
                super.calculateWorkingSpeed(this.model);
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("Thread interrupted: " + Thread.currentThread().getName());
                    return;
                }
            }


        } catch (IOException e) {
            super.messageList.addMessage(e.getMessage());
        }
    }


    public void copyFileWithFileChannel(Path sourceFile, Path targetFile, long totalSize) {
        super.setLastProcessedSize(0);
        try (FileChannel inputChannel = FileChannel.open(sourceFile, StandardOpenOption.READ);
             FileChannel outputChannel = FileChannel.open(targetFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {


            ByteBuffer buffer;

            if(inputChannel.size() < 5*1024*1024) {
                buffer = ByteBuffer.allocateDirect((int)super.DEFAULT_BUFFERSIZE);
            }
            else{
                buffer = ByteBuffer.allocate((int)super.calculateBufferSize(inputChannel.size()));
            }
            //System.out.println("transferBufferSize: " + transferBufferSize + " fileSize: " + fileSize);

            while (inputChannel.read(buffer) > -1) {
                buffer.flip();
                int bytesTransferred = outputChannel.write(buffer);
                super.addFileProcessedSize(bytesTransferred);
                buffer.clear();

                if(inputChannel.size() < 50*1024*1024) {
                    continue;
                }
                else{
                    super.updateProgressBar(this.model);
                    super.calculateWorkingSpeed(this.model);
                }

            }


        } catch (IOException e) {
            super.messageList.addMessage(e.getMessage());
        }
    }


    public BaseModel getModel() {
        return model;
    }
}

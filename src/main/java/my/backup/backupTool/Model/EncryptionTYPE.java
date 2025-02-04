package my.backup.backupTool.Model;

public enum EncryptionTYPE {
    AES_CBC("AES-CBC");

    private final String name;

    EncryptionTYPE(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}

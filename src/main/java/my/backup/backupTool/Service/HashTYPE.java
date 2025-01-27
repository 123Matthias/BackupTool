package my.backup.backupTool.Service;

public enum HashTYPE {
    CRC32("CRC32"),
    SHA256("SHA-256");

    private final String name;

    HashTYPE(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}

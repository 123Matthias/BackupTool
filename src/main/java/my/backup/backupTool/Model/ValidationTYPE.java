package my.backup.backupTool.Model;

public enum ValidationTYPE {
    CRC32("CRC32"),
    SHA256("SHA-256");

    private final String name;

    ValidationTYPE(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}

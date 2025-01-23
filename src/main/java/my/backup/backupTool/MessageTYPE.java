package my.backup.backupTool;

public enum MessageTYPE {
    VALIDATION("Check your Input values"),
    ALERT("ALERT");

    private final String name;

    MessageTYPE(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}

package my.backup.backupTool;

public enum Theme {
    LIGHT("css/lightTheme.css"),
    DARK("css/darkTheme.css");

        private final String name;

        Theme(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

}

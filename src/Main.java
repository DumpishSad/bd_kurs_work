import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        System.setProperty("log4j.configurationFile", "log4j2.xml");
        SwingUtilities.invokeLater(() -> {
            new App();
        });
    }
}
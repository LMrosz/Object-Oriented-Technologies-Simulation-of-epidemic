import javax.swing.SwingUtilities;

/** Aplikacja kliencka: uruchamia okno symulacji zakażeń. */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainWindow().setVisible(true));
    }
}

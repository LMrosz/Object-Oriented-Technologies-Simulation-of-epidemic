import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.IOException;
import java.nio.file.Path;

/** Okno aplikacji: wizualizacja ruchu i zakażeń oraz sterowanie symulacją i zapisem stanu. */
public class MainWindow extends JFrame {
    private static final int TICK_MS = 40; // 25 kroków na sekundę = czas rzeczywisty

    private Simulation simulation;
    private SimulationCareTaker careTaker;
    private final SimulationPanel panel;
    private final Timer timer;
    private final JLabel statusLabel = new JLabel();
    private final JButton startPauseButton = new JButton("Start");
    private final JComboBox<String> speedBox = new JComboBox<>(new String[]{"x1", "x2", "x5", "x10"});
    private final DefaultComboBoxModel<String> checkpointModel = new DefaultComboBoxModel<>();
    private final JComboBox<String> checkpointBox = new JComboBox<>(checkpointModel);
    {
        checkpointBox.setPrototypeDisplayValue("#00  (z pliku, t = 000.0 s)");
    }
    private int stepsPerTick = 1;

    public MainWindow() {
        super("Symulacja zakażeń - Stan i Pamiątka");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        simulation = new Simulation(caseConfig(2));
        careTaker = new SimulationCareTaker(simulation);
        panel = new SimulationPanel(simulation);
        timer = new Timer(TICK_MS, e -> tick());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        JButton case1 = new JButton("Przypadek 1 (bez odporności)");
        JButton case2 = new JButton("Przypadek 2 (część odporna)");
        top.add(startPauseButton);
        top.add(new JLabel("Prędkość:"));
        top.add(speedBox);
        top.add(Box.createHorizontalStrut(10));
        top.add(case1);
        top.add(case2);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        JButton saveCheckpoint = new JButton("Zapisz punkt");
        JButton restoreCheckpoint = new JButton("Przywróć punkt");
        JButton saveFile = new JButton("Zapisz do pliku...");
        JButton loadFile = new JButton("Wczytaj z pliku...");
        bottom.add(saveCheckpoint);
        bottom.add(checkpointBox);
        bottom.add(restoreCheckpoint);
        bottom.add(Box.createHorizontalStrut(10));
        bottom.add(saveFile);
        bottom.add(loadFile);

        JPanel south = new JPanel(new BorderLayout());
        south.add(bottom, BorderLayout.NORTH);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(2, 10, 6, 10));
        south.add(statusLabel, BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        startPauseButton.addActionListener(e -> toggleRunning());
        speedBox.addActionListener(e -> stepsPerTick = Integer.parseInt(((String) speedBox.getSelectedItem()).substring(1)));
        case1.addActionListener(e -> startNewSimulation(1));
        case2.addActionListener(e -> startNewSimulation(2));
        saveCheckpoint.addActionListener(e -> saveCheckpoint());
        restoreCheckpoint.addActionListener(e -> restoreCheckpoint());
        saveFile.addActionListener(e -> saveToFile());
        loadFile.addActionListener(e -> loadFromFile());

        updateStatus();
        pack();
        setLocationRelativeTo(null);
    }

    /** Konfiguracja przypadku: 1 - nikt nie jest odporny, 2 - część początkowej populacji i wchodzących jest odporna. */
    static SimulationConfig caseConfig(int number) {
        SimulationConfig d = new SimulationConfig();
        double immune = number == 1 ? 0.0 : d.getImmuneRatio();
        return new SimulationConfig(d.getAreaWidth(), d.getAreaHeight(), d.getMaxSpeed(), d.getStepsPerSecond(),
                d.getBorderExitProb(), d.getEntryInfectionProb(), d.getInfectionRadius(), d.getRequiredContactSteps(),
                d.getInfectionMinSteps(), d.getInfectionMaxSteps(), d.getInitialPopulation(), d.getSpawnRate(), immune);
    }

    private void tick() {
        for (int i = 0; i < stepsPerTick; i++) {
            simulation.step();
        }
        refresh();
    }

    private void toggleRunning() {
        if (timer.isRunning()) {
            timer.stop();
            startPauseButton.setText("Start");
        } else {
            timer.start();
            startPauseButton.setText("Pauza");
        }
    }

    private void startNewSimulation(int number) {
        simulation = new Simulation(caseConfig(number));
        careTaker = new SimulationCareTaker(simulation);
        checkpointModel.removeAllElements();
        panel.setSimulation(simulation);
        refresh();
    }

    private void saveCheckpoint() {
        int number = careTaker.create();
        checkpointModel.addElement(String.format("#%d  (t = %.1f s)", number, simulation.getTimeSeconds()));
        checkpointBox.setSelectedIndex(number);
    }

    private void restoreCheckpoint() {
        int index = checkpointBox.getSelectedIndex();
        if (index < 0) {
            JOptionPane.showMessageDialog(this, "Najpierw zapisz punkt.", "Brak punktu zapisu", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        careTaker.restore(index);
        refresh();
    }

    private void saveToFile() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try {
            careTaker.saveToFile(chooser.getSelectedFile().toPath());
        } catch (IOException ex) {
            showError("Nie udało się zapisać pliku: " + ex.getMessage());
        }
    }

    private void loadFromFile() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        Path path = chooser.getSelectedFile().toPath();
        try {
            careTaker.loadFromFile(path);
            checkpointModel.addElement(String.format("#%d  (z pliku, t = %.1f s)", careTaker.size() - 1, simulation.getTimeSeconds()));
            refresh();
        } catch (IOException | ClassNotFoundException | ClassCastException ex) {
            showError("Nie udało się wczytać pliku: " + ex.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Błąd", JOptionPane.ERROR_MESSAGE);
    }

    private void refresh() {
        panel.repaint();
        updateStatus();
    }

    private void updateStatus() {
        int infected = simulation.count(StateType.INFECTED_SYMPTOMATIC) + simulation.count(StateType.INFECTED_ASYMPTOMATIC);
        statusLabel.setText(String.format("t = %.1f s (krok %d)   osobników: %d   zdrowych: %d   zarażonych: %d (z objawami: %d)   odpornych: %d",
                simulation.getTimeSeconds(), simulation.getStepNumber(), simulation.getPersons().size(),
                simulation.count(StateType.HEALTHY), infected,
                simulation.count(StateType.INFECTED_SYMPTOMATIC), simulation.count(StateType.IMMUNE)));
    }
}

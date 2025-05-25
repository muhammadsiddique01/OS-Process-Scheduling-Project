import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ProcessSchedulerGUI extends JFrame {
    private JTextField txtArrivalTime, txtBurstTime, txtPriority, txtQuantum;
    private JButton btnAddProcess, btnExecute;
    private JComboBox<String> algorithmDropdown;
    private JPanel ganttChartPanel;
    private JTable processTable;
    private List<Process> processes;
    private JLabel lblAverageTimes;
    private int processId = 1;

    public ProcessSchedulerGUI() {
        setTitle("Process Scheduling Algorithms");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        processes = new ArrayList<>();

        JPanel panelInput = new JPanel(new GridLayout(5, 2));
        panelInput.add(new JLabel("Arrival Time:"));
        txtArrivalTime = new JTextField();
        panelInput.add(txtArrivalTime);

        panelInput.add(new JLabel("Burst Time:"));
        txtBurstTime = new JTextField();
        panelInput.add(txtBurstTime);

        panelInput.add(new JLabel("Priority:"));
        txtPriority = new JTextField();
        panelInput.add(txtPriority);

        panelInput.add(new JLabel("Quantum (for Round Robin):"));
        txtQuantum = new JTextField();
        panelInput.add(txtQuantum);

        btnAddProcess = new JButton("Add Process");
        panelInput.add(btnAddProcess);

        JPanel panelButtons = new JPanel();
        panelButtons.setLayout(new FlowLayout());

        btnExecute = new JButton("Execute");
        panelButtons.add(btnExecute);

        algorithmDropdown = new JComboBox<>(new String[]{"FCFS", "SJF Non-Preemptive", "Priority Preemptive", "Round Robin"});
        panelButtons.add(algorithmDropdown);

        add(panelInput, BorderLayout.NORTH);
        add(panelButtons, BorderLayout.CENTER);

        ganttChartPanel = new JPanel();
        ganttChartPanel.setPreferredSize(new Dimension(750, 200));

        processTable = new JTable();
        JScrollPane scrollPaneTable = new JScrollPane(processTable);
        scrollPaneTable.setPreferredSize(new Dimension(750, 150));

        lblAverageTimes = new JLabel();
        lblAverageTimes.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel panelOutput = new JPanel(new BorderLayout());
        panelOutput.add(ganttChartPanel, BorderLayout.NORTH);
        panelOutput.add(scrollPaneTable, BorderLayout.CENTER);
        panelOutput.add(lblAverageTimes, BorderLayout.SOUTH);

        add(panelOutput, BorderLayout.SOUTH);

        btnAddProcess.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int arrivalTime = txtArrivalTime.getText().trim().isEmpty() ? 0 : Integer.parseInt(txtArrivalTime.getText().trim());
                    int burstTime = Integer.parseInt(txtBurstTime.getText().trim());
                    int priority = txtPriority.getText().trim().isEmpty() ? 0 : Integer.parseInt(txtPriority.getText().trim());
                    processes.add(new Process(processId++, arrivalTime, burstTime, priority));
                    txtArrivalTime.setText("");
                    txtBurstTime.setText("");
                    txtPriority.setText("");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Please enter valid integers for Arrival Time, Burst Time, and Priority.", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnExecute.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executeAlgorithm();
            }
        });

        algorithmDropdown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executeAlgorithm();
            }
        });
    }

    private void executeAlgorithm() {
        String selectedAlgorithm = (String) algorithmDropdown.getSelectedItem();
        Scheduler scheduler = new Scheduler(processes);
        switch (selectedAlgorithm) {
            case "FCFS":
                scheduler.fcfs();
                break;
            case "SJF Non-Preemptive":
                scheduler.sjfPreemptive();
                break;
            case "Priority Preemptive":
                scheduler.priorityPreemptive();
                break;
            case "Round Robin":
                try {
                    int quantum = Integer.parseInt(txtQuantum.getText().trim());
                    if (quantum <= 0) {
                        throw new NumberFormatException();
                    }
                    scheduler.roundRobinWithPriority(quantum);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid positive integer for Quantum.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                break;
        }
        displayResults(scheduler);
    }

    private void displayResults(Scheduler scheduler) {
        List<Process> scheduledProcesses = scheduler.getProcesses();

        lblAverageTimes.setText(String.format("Average Waiting Time: %.2f    Average Turnaround Time: %.2f",
                scheduler.getAverageWaitingTime(), scheduler.getAverageTurnaroundTime()));
        lblAverageTimes.setForeground(Color.BLUE);

        ganttChartPanel.removeAll();
        GanttChartGenerator ganttChart = new GanttChartGenerator(scheduledProcesses);
        ganttChartPanel.add(ganttChart);
        ganttChartPanel.revalidate();
        ganttChartPanel.repaint();

        String[] columnNames = {"Job", "Arrival Time", "Burst Time", "Finish Time", "Turnaround Time", "Waiting Time"};
        Object[][] data = new Object[scheduledProcesses.size()][6];

        for (int i = 0; i < scheduledProcesses.size(); i++) {
            Process process = scheduledProcesses.get(i);
            data[i][0] = "P" + process.getId();
            data[i][1] = process.getArrivalTime();
            data[i][2] = process.getBurstTime();
            data[i][3] = process.getFinishTime();
            data[i][4] = process.getTurnaroundTime();
            data[i][5] = process.getWaitingTime();
        }

        processTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ProcessSchedulerGUI frame = new ProcessSchedulerGUI();
            frame.setVisible(true);
        });
    }
}
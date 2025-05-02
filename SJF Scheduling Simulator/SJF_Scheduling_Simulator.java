package sjf_scheduling_simulator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;


public class SJF_Scheduling_Simulator extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField numField;
    private JButton setButton, calcButton;
    private JPanel inputPanel;
    private JLabel resultLabel;
    private ArrayList<JTextField[]> processFields = new ArrayList<>();

    public SJF_Scheduling_Simulator() {
        setTitle("SJF Scheduling Simulator");
        setSize(900, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header Panel (Operating Systems | Interim Laboratory)
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel headerLabel = new JLabel("Operating Systems | Interim Laboratory");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setForeground(Color.BLACK);
        headerPanel.add(headerLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Main Panel to hold both the input and results
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        add(new JScrollPane(mainPanel), BorderLayout.CENTER);

        // Input Panel (process count)
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5)); 
        topPanel.add(new JLabel("Number of Processes:"));
        numField = new JTextField(5);
        topPanel.add(numField);
        setButton = new JButton("Set");
        calcButton = new JButton("Calculate SJF");
        topPanel.add(setButton);
        topPanel.add(calcButton);
        mainPanel.add(topPanel);

        // Input fields panel
        inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        JScrollPane inputScroll = new JScrollPane(inputPanel);
        inputScroll.setPreferredSize(new Dimension(700, 200));
        mainPanel.add(inputScroll);

        // Table for results
        tableModel = new DefaultTableModel(new String[]{"PID", "AT", "BT", "ST", "ET", "TT", "WT"}, 0);
        table = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setPreferredSize(new Dimension(700, 150));
        mainPanel.add(tableScroll);

        // Result label
        resultLabel = new JLabel("Average TAT: ---, Average WT: ---");
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 14));
        resultLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(resultLabel);

        // Developer Panel at the very bottom
        JPanel developerPanel = new JPanel();
        developerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel developerLabel = new JLabel("Developers: Barroca | Cajigal");
        developerLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        developerLabel.setForeground(Color.DARK_GRAY);
        developerPanel.add(developerLabel);
        add(developerPanel, BorderLayout.SOUTH);

        // Button Actions
        setButton.addActionListener(e -> createInputFields());
        calcButton.addActionListener(e -> calculateSJF());

        setVisible(true);
    }

    private void createInputFields() {
        inputPanel.removeAll();
        processFields.clear();

        int num;
        try {
            num = Integer.parseInt(numField.getText());
            if (num < 1) {
                JOptionPane.showMessageDialog(this, "Please enter at least 1 process.");
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number of processes.");
            return;
        }

        for (int i = 0; i < num; i++) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));  
            row.add(new JLabel("P" + (i + 1) + " AT:"));
            JTextField atField = new JTextField(5);
            row.add(atField);
            row.add(new JLabel("BT:"));
            JTextField btField = new JTextField(5);
            row.add(btField);
            inputPanel.add(row);
            processFields.add(new JTextField[]{atField, btField});
        }

        inputPanel.revalidate();
        inputPanel.repaint();
    }

    private void calculateSJF() {
        int prc = processFields.size();

        // Arrays to store the data for processes
        int[] id = new int[prc];
        int[] at = new int[prc]; // Arrival time
        int[] bt = new int[prc]; // Burst time
        int[] et = new int[prc]; // End time (Completion time)
        int[] stime = new int[prc]; // Start time
        int[] tt = new int[prc]; // Turnaround time
        int[] wt = new int[prc]; // Waiting time
        int[] f = new int[prc]; // Flag to track if a process is finished

        int st = 0, tot = 0; // Start time counter and process completion count
        float avgwt = 0, avgtt = 0; // Average waiting time and average turnaround time

        // Populate process data from input fields
        for (int i = 0; i < prc; i++) {
            try {
                at[i] = Integer.parseInt(processFields.get(i)[0].getText());
                bt[i] = Integer.parseInt(processFields.get(i)[1].getText());
                id[i] = i + 1; // Process ID
                f[i] = 0; // Process is initially not finished
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid Arrival and Burst times.");
                return;
            }
        }

        // Scheduling the processes using SJF
        while (tot < prc) {
            int c = prc, min = Integer.MAX_VALUE; // Min burst time initially set to a large value

            // Find the process with the shortest burst time that is ready to execute
            for (int i = 0; i < prc; i++) {
                if ((at[i] <= st) && (f[i] == 0) && (bt[i] < min)) {
                    min = bt[i];
                    c = i;
                }
            }

            if (c == prc) { // If no process is ready to execute, just increment the time
                st++;
            } else { // If a process is found, execute it
                stime[c] = st; // Set the start time
                et[c] = st + bt[c]; // Set the end time (completion time)
                st = et[c]; // Update current time
                tt[c] = et[c] - at[c]; // Turnaround time = end time - arrival time
                wt[c] = stime[c] - at[c]; // Waiting time = start time - arrival time
                f[c] = 1; // Mark process as finished
                tot++; // Increment the number of finished processes
            }
        }

        // Update the table with the results
        tableModel.setRowCount(0);
        for (int i = 0; i < prc; i++) {
            avgtt += tt[i]; // Sum up the turnaround times
            avgwt += wt[i]; // Sum up the waiting times
            tableModel.addRow(new Object[]{
                id[i], at[i], bt[i], stime[i], et[i], tt[i], wt[i]
            });
        }

        // Update the result label with the averages
        resultLabel.setText(String.format("Average Turnaround Time: %.2f    |    Average Waiting Time: %.2f",
                avgtt / prc, avgwt / prc));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SJF_Scheduling_Simulator());
    }
}

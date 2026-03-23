package gui;

import businesslogic.TaskManagement;
import businesslogic.Utility;
import dataaccess.DataPersister;
import datamodel.ComplexTask;
import datamodel.Employee;
import datamodel.SimpleTask;
import datamodel.Task;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

public class ProjectManagerGUI extends JFrame {
    private TaskManagement taskManagement;

    public ProjectManagerGUI() {

        taskManagement = DataPersister.load();

        setTitle("Project Management Dashboard");
        setSize(850, 650);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                DataPersister.save(taskManagement);
                System.exit(0);
            }
        });

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Manage Assignments", createAssignmentPanel());
        tabbedPane.addTab("View Employees & Tasks", createViewDataPanel());
        tabbedPane.addTab("Update Task Status", createUpdateStatusPanel());
        tabbedPane.addTab("Project Statistics", createStatisticsPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createAssignmentPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel employeePanel = new JPanel(new GridLayout(3, 2, 5, 5));
        employeePanel.setBorder(BorderFactory.createTitledBorder("1. Add Employee"));
        JTextField empIdField = new JTextField();
        JTextField empNameField = new JTextField();
        JButton addEmpBtn = new JButton("Add Employee");

        employeePanel.add(new JLabel("Employee ID:"));
        employeePanel.add(empIdField);
        employeePanel.add(new JLabel("Employee Name:"));
        employeePanel.add(empNameField);
        employeePanel.add(new JLabel("")); // spacer
        employeePanel.add(addEmpBtn);

        JPanel taskPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        taskPanel.setBorder(BorderFactory.createTitledBorder("2. Add & Assign Task"));

        JTextField assignEmpIdField = new JTextField();
        JTextField taskIdField = new JTextField();
        JComboBox<String> taskTypeBox = new JComboBox<>(new String[]{"SimpleTask", "ComplexTask"});
        JTextField startHourField = new JTextField();
        JTextField endHourField = new JTextField();
        JButton assignTaskBtn = new JButton("Assign Task");

        taskPanel.add(new JLabel("Assign to Employee ID:"));
        taskPanel.add(assignEmpIdField);
        taskPanel.add(new JLabel("Task ID:"));
        taskPanel.add(taskIdField);
        taskPanel.add(new JLabel("Task Type:"));
        taskPanel.add(taskTypeBox);
        taskPanel.add(new JLabel("Start Hour (Simple Only):"));
        taskPanel.add(startHourField);
        taskPanel.add(new JLabel("End Hour (Simple Only):"));
        taskPanel.add(endHourField);
        taskPanel.add(new JLabel(""));
        taskPanel.add(assignTaskBtn);

        addEmpBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(empIdField.getText());
                String name = empNameField.getText();
                taskManagement.addEmployee(new Employee(id, name));
                JOptionPane.showMessageDialog(this, "Employee Added!");
                empIdField.setText(""); empNameField.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid Employee Input.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        assignTaskBtn.addActionListener(e -> {
            try {
                int empId = Integer.parseInt(assignEmpIdField.getText());
                int taskId = Integer.parseInt(taskIdField.getText());
                String type = (String) taskTypeBox.getSelectedItem();

                Task newTask;
                if ("SimpleTask".equals(type)) {
                    int start = Integer.parseInt(startHourField.getText());
                    int end = Integer.parseInt(endHourField.getText());
                    newTask = new SimpleTask(taskId, "Uncompleted", start, end);
                } else {
                    ComplexTask complexTask = new ComplexTask(taskId, "Uncompleted");
                    openComplexTaskDialog(complexTask);
                    newTask = complexTask;
                }

                taskManagement.assignTaskToEmployee(empId, newTask);
                JOptionPane.showMessageDialog(this, "Task Assigned!");

                taskIdField.setText(""); startHourField.setText(""); endHourField.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid Task Input.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(employeePanel);
        panel.add(taskPanel);
        return panel;
    }

    private JPanel createViewDataPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea displayArea = new JTextArea();
        displayArea.setEditable(false);
        JButton refreshBtn = new JButton("Refresh View");

        refreshBtn.addActionListener(e -> {
            displayArea.setText("");
            Map<Employee, List<Task>> data = taskManagement.getProjectData();

            if (data == null || data.isEmpty()) {
                displayArea.append("No data available.\n");
                return;
            }

            for (Map.Entry<Employee, List<Task>> entry : data.entrySet()) {
                Employee emp = entry.getKey();
                int totalDuration = taskManagement.calculateEmployeeWorkDuration(emp.getIdEmployee());

                displayArea.append("Employee: " + emp.getName() + " (ID: " + emp.getIdEmployee() + ") - Total Est. Duration: " + totalDuration + " hrs\n");

                for (Task task : entry.getValue()) {
                    displayArea.append("   -> Task ID: " + task.getIdTask() + " | Status: " + task.getStatusTask() + " | Est. Duration: " + task.estimateDuration() + " hrs\n");
                }
                displayArea.append("--------------------------------------------------\n");
            }
        });

        panel.add(refreshBtn, BorderLayout.NORTH);
        panel.add(new JScrollPane(displayArea), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createUpdateStatusPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField taskIdField = new JTextField(15);
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Completed", "Uncompleted"});
        JButton updateBtn = new JButton("Update Status");

        JTextField empIdField = new JTextField(15);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Employee ID:"), gbc);
        gbc.gridx = 1;
        panel.add(empIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Task ID:"), gbc);
        gbc.gridx = 1;
        panel.add(taskIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("New Status:"), gbc);
        gbc.gridx = 1;
        panel.add(statusBox, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        panel.add(updateBtn, gbc);

        updateBtn.addActionListener(e -> {
            try {
                int empId = Integer.parseInt(empIdField.getText());
                int taskId = Integer.parseInt(taskIdField.getText());
                String newStatus = (String) statusBox.getSelectedItem();

                taskManagement.modifyTaskStatus(empId, taskId, newStatus);
                JOptionPane.showMessageDialog(this, "Status Updated!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid Input.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea statsArea = new JTextArea();
        statsArea.setEditable(false);
        JButton loadStatsBtn = new JButton("Load Statistics");

        loadStatsBtn.addActionListener(e -> {
            statsArea.setText("Task Statistics  \n");
            Utility utility = new Utility();

            // get task statistics map
            //map inside a map: outer map separates by status, inner map separates by task
            Map<String, Map<String, Integer>> statsMap = utility.getTasksStatistics(taskManagement.getProjectData());

            for (Map.Entry<String, Map<String, Integer>> statusEntry : statsMap.entrySet()) {
                statsArea.append("Status: " + statusEntry.getKey() + "\n");
                for (Map.Entry<String, Integer> typeEntry : statusEntry.getValue().entrySet()) {
                    statsArea.append("  - " + typeEntry.getKey() + ": " + typeEntry.getValue() + "\n");
                }
            }

            statsArea.append("\n Hardworking Employees (>40 hrs)\n");

            PrintStream originalOut = System.out;// to save the console
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PrintStream newOut = new PrintStream(baos);
                System.setOut(newOut);

                utility.displayHardworkingEmployees(taskManagement, taskManagement.getProjectData());

                System.out.flush();
                statsArea.append(baos.toString());
            } finally {
                System.setOut(originalOut); //restore normal console
            }
        });

        panel.add(loadStatsBtn, BorderLayout.NORTH);
        panel.add(new JScrollPane(statsArea), BorderLayout.CENTER);
        return panel;
    }

    private void openComplexTaskDialog(ComplexTask complexTask) {

        JDialog dialog = new JDialog(this, "Manage Sub-Tasks", true);
        dialog.setSize(400, 450);
        dialog.setLayout(new GridLayout(9, 2, 10, 10));
        dialog.setLocationRelativeTo(this);

        JTextField subTaskIdField = new JTextField();
        JTextField startHourField = new JTextField();
        JTextField endHourField = new JTextField();
        JButton addSubTaskBtn = new JButton("Add Sub-Task");

        JTextField removeTaskIdField = new JTextField();
        JButton removeSubTaskBtn = new JButton("Remove Sub-Task");

        JButton doneBtn = new JButton("Done");

        dialog.add(new JLabel("ADD SUB-TASK"));
        dialog.add(new JLabel(""));

        dialog.add(new JLabel(" Sub-Task ID:"));
        dialog.add(subTaskIdField);

        dialog.add(new JLabel(" Start Hour:"));
        dialog.add(startHourField);

        dialog.add(new JLabel(" End Hour:"));
        dialog.add(endHourField);

        dialog.add(new JLabel(""));
        dialog.add(addSubTaskBtn);

        dialog.add(new JLabel("REMOVE SUB-TASK"));
        dialog.add(new JLabel(""));

        dialog.add(new JLabel(" Task ID to Remove:"));
        dialog.add(removeTaskIdField);

        dialog.add(new JLabel(""));
        dialog.add(removeSubTaskBtn);

        dialog.add(new JLabel(""));
        dialog.add(doneBtn);

        addSubTaskBtn.addActionListener(e -> {
            try {
                int subId = Integer.parseInt(subTaskIdField.getText());
                int start = Integer.parseInt(startHourField.getText());
                int end = Integer.parseInt(endHourField.getText());

                SimpleTask subTask = new SimpleTask(subId, "Uncompleted", start, end);
                complexTask.addTask(subTask);

                JOptionPane.showMessageDialog(dialog, "Sub-task added successfully!");
                subTaskIdField.setText(""); startHourField.setText(""); endHourField.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid input. Please use numbers.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        removeSubTaskBtn.addActionListener(e -> {
            try {
                int removeId = Integer.parseInt(removeTaskIdField.getText());
                boolean success = complexTask.removeTask(removeId);

                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Sub-task " + removeId + " removed successfully!");
                    removeTaskIdField.setText("");
                } else {
                    JOptionPane.showMessageDialog(dialog, "Task ID not found in this Complex Task.", "Not Found", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid input. Please enter a valid Task ID.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        doneBtn.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ProjectManagerGUI().setVisible(true);
        });
    }
}
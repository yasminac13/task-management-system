package org.example;

import businesslogic.TaskManagement;
import businesslogic.Utility;
import datamodel.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

        TaskManagement mgmt = new TaskManagement();


        Employee alice = new Employee(1, "Alice");
        Employee bob = new Employee(2, "Bob");

        mgmt.addEmployee(alice);
        mgmt.addEmployee(bob);

        SimpleTask simple = new SimpleTask(10, "Uncompleted", 8, 13);
        mgmt.assignTaskToEmployee(1, simple);

        ComplexTask complex = new ComplexTask(20, "Uncompleted");

        complex.addTask(new SimpleTask(21, "Uncompleted", 0, 20));
        complex.addTask(new SimpleTask(22, "Uncompleted", 0, 25));

        mgmt.assignTaskToEmployee(2, complex);

        System.out.println("--- INITIAL STATE ---");
        printProjectStatus(mgmt);

        System.out.println("\nModifying statuses to 'Completed'...");
        mgmt.modifyTaskStatus(1, 10, "Completed");
        mgmt.modifyTaskStatus(2, 20, "Completed");

        System.out.println("\n--- UPDATED STATE ---");
        printProjectStatus(mgmt);

        System.out.println("\n--- UTILITY: STATISTICS ---");
        Map<String, Map<String, Integer>> stats = Utility.getTasksStatistics(mgmt.getProjectData());
        System.out.println("Task Counts per Employee: " + stats);

        System.out.println("\n--- UTILITY: HARDWORKING EMPLOYEES (>40h) ---");
        Utility.displayHardworkingEmployees(mgmt, mgmt.getProjectData());
    }

    private static void printProjectStatus(TaskManagement mgmt) {
        Map<Employee, List<Task>> data = mgmt.getProjectData();
        for (Map.Entry<Employee, List<Task>> entry : data.entrySet()) {
            Employee emp = entry.getKey();
            System.out.println("Employee: " + emp.getName() + " (ID: " + emp.getIdEmployee() + ")");

            for (Task t : entry.getValue()) {
                String type = (t instanceof ComplexTask) ? "Complex" : "Simple";
                System.out.println("  [" + type + "] Task ID: " + t.getIdTask() +
                        ", Status: " + t.getStatusTask() +
                        ", Duration: " + t.estimateDuration() + "h");
            }
        }
    }
}
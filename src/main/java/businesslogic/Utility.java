package businesslogic;

import datamodel.Employee;
import datamodel.Task;

import java.util.*;

public class Utility {
    public static void displayHardworkingEmployees(TaskManagement management, Map<Employee, List<Task>> projectData) {

        List <EmployeeDuration> list = new ArrayList<>();

        for(Employee employee : projectData.keySet()){
            int duration = management.calculateEmployeeWorkDuration(employee.getIdEmployee());

            if(duration>40){
                list.add(new EmployeeDuration(duration, employee.getName()));
            }
        }

        list.sort(Comparator.comparingInt(ed->ed.duration));

        System.out.println("Employees with 40> work duration: ");
        for(EmployeeDuration ed: list){
            System.out.println(ed.name + " (" + ed.duration + " hours)");
        }
    }

    public static Map<String, Map<String, Integer>> getTasksStatistics(Map<Employee, List<Task>> projectData){
        Map<String, Map<String, Integer>> result = new HashMap<>();
        //employee name , task status, how many tasks have that status

        for(Map.Entry<Employee, List<Task>> entry : projectData.entrySet()){
            String name = entry.getKey().getName();
            List<Task> tasks = entry.getValue();

            Map<String, Integer> map = new HashMap<>();
            map.put("Completed",0);
            map.put("Uncompleted",0);

            for(Task task : tasks){
                String status = task.getStatusTask();
                if(map.containsKey(status)){
                    map.put(status,map.get(status)+1);
                }
            }

            result.put(name,map);
        }

        return result;
    }

    private static class EmployeeDuration {
        private int duration;
        private String name;

        public EmployeeDuration(int duration, String name) {
            this.duration = duration;
            this.name = name;
        }
    }
}

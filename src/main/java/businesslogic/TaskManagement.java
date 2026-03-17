package businesslogic;


import datamodel.Employee;
import datamodel.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManagement {
    private Map<Employee, List<Task>> map = new HashMap<Employee, List<Task>>();

    public void assignTaskToEmployee(int idEmployee, Task task) {
        for (Employee employee : map.keySet()) {
            if(employee.getIdEmployee()==idEmployee){
                map.get(employee).add(task);
            }
        }
    }

    public int calculateEmployeeWorkDuration(int idEmployee) {
        int duration = 0;

        for (Map.Entry<Employee, List<Task>> entry : map.entrySet()) {
            Employee employee = entry.getKey();

            if(employee.getIdEmployee()==idEmployee){
                List<Task> tasks = entry.getValue();
                for(Task task : tasks){
                    if("Completed".equalsIgnoreCase(task.getStatusTask())){
                        duration+= task.estimateDuration();
                    }
                }
                break;
            }
        }

        return duration;
    }



    public void modifyTaskStatus(int idEmployee, int idTask, String statusTask) {
        for(Map.Entry<Employee, List<Task>> entry : map.entrySet()) {
            Employee employee = entry.getKey();

            if(employee.getIdEmployee()==idEmployee){
                List<Task> tasks = entry.getValue();
                for(Task task : tasks){
                    if(task.getIdTask()==idTask){
                        task.setStatusTask(statusTask);
                    }
                }
            }
        }
    }

    public Map<Employee, List<Task>> getProjectData() {
        return this.map;
    }

    public void addEmployee(Employee employee) {
        map.putIfAbsent(employee, new ArrayList<>());
    }
}

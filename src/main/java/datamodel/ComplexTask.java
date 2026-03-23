package datamodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class ComplexTask extends Task implements Serializable {
    private List<Task> tasks = new ArrayList<Task>();

    public ComplexTask(int idEmployee, String name) {
        super(idEmployee, name);
    }

    public void addTask(Task task) {
        if(tasks!=null) tasks.add(task);
    }

    public boolean removeTask(int taskId) {
        return tasks.removeIf(task -> task.getIdTask() == taskId);
    }

    @Override
    public int estimateDuration() {
        int duration = 0;
        for (Task t : tasks) duration += t.estimateDuration();
        return duration;
    }


}

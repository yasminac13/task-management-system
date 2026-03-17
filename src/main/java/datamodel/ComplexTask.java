package datamodel;

import java.util.ArrayList;
import java.util.List;

public final class ComplexTask extends Task {
    private List<Task> tasks = new ArrayList<Task>();

    public ComplexTask(int idEmployee, String name) {
        super(idEmployee, name);
    }

    public void addTask(Task task) {
        if(tasks!=null) tasks.add(task);
    }

    @Override
    public int estimateDuration() {
        int duration = 0;
        for (Task t : tasks) duration += t.estimateDuration();
        return duration;
    }


}

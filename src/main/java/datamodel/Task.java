package datamodel;

public sealed abstract class Task permits SimpleTask, ComplexTask {
    private int idTask;
    private String statusTask = "Uncompleted";

    public Task(int idTask, String statusTask) {
        this.idTask = idTask;
        this.statusTask = statusTask;
    }
    public int getIdTask() {
        return idTask;
    }
    public void setIdTask(int idTask) {
        this.idTask = idTask;
    }
    public String getStatusTask() {
        return statusTask;
    }
    public void setStatusTask(String statusTask) {
        this.statusTask = statusTask;
    }

    public abstract int estimateDuration();
}


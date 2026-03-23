package datamodel;

import java.io.Serializable;

public final    class SimpleTask extends Task implements Serializable {
    private int startHour;
    private int endHour;

    public SimpleTask(int idEmployee, String name, int startHour, int endHour) {
        super(idEmployee, name);
        this.startHour = startHour;
        this.endHour = endHour;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getEndHour() {
        return endHour;
    }
    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }
    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    @Override
    public int estimateDuration() {
        return endHour - startHour;
    }
}

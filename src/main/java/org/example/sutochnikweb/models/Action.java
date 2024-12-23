package org.example.sutochnikweb.models;

// Класс действия (операции)
public class Action {
    private final ActionType type;
    private int start;
    private int end;
    private final int duration;
    private int operationNumber;
    private final int otherNumInfo;
    private final String otherInfo;
    private boolean completed;

    public Action(ActionType type, int start, int end, int duration, int otherNumInfo, String otherInfo, boolean completed) {
        this.type = type;
        this.start = start;
        this.end = end;
        this.duration = duration;
        this.otherNumInfo = otherNumInfo;
        this.otherInfo = otherInfo;
        this.completed = completed;
    }

    public Action(ActionType type, int start, int end, int duration, int otherNumInfo, String otherInfo) {
        this(type, start, end, duration, otherNumInfo, otherInfo, true);
    }

    public Action(ActionType type, int start, int end, int duration, boolean completed) {
        this(type, start, end, duration, 0, "", completed);
    }

    public Action(ActionType type, int start, int end, int duration) {
        this(type, start, end, duration, 0, "");
    }

    public Action(ActionType type, int start, int end, int duration, int otherNumInfo) {
        this(type, start, end, duration, otherNumInfo, "");
    }

    public Action(ActionType type, int start, int end, int duration, String otherInfo) {
        this(type, start, end, duration, 0, otherInfo);
    }

    public ActionType getType() {
        return type;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int getDuration() {
        return duration;
    }

    public int getOperationNumber() {
        return operationNumber;
    }

    public int getOtherNumInfo() {
        return otherNumInfo;
    }

    public String getOtherInfo() {
        return otherInfo;
    }

    public boolean getCompleted() {
        return completed;
    }

    public void setOperationNumber(int operationNumber) {
        this.operationNumber = operationNumber;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void adjustTimes(int startTimeInMillis) {
        int millisecondsInDay = 86400000; // количество миллисекунд в дне
        this.start = (this.start + startTimeInMillis) % millisecondsInDay;
        this.end = (this.end + startTimeInMillis) % millisecondsInDay;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "Action{" +
                "type=" + type +
                ", start=" + Double.parseDouble(String.valueOf(start)) / 60 / 1000 +
                ", end=" + Double.parseDouble(String.valueOf(end)) / 60 / 1000 +
                ", duration=" + duration +
                ", operationNumber=" + operationNumber +
                ", otherNumInfo=" + otherNumInfo +
                ", otherInfo='" + otherInfo + '\'' +
                '}';
    }
}
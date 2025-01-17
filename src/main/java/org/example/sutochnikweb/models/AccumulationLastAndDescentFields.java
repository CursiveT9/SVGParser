package org.example.sutochnikweb.models;

public class AccumulationLastAndDescentFields {
    private Integer count;
    private double avgDuration;

    public AccumulationLastAndDescentFields() {
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Double getAvgDuration() {
        return avgDuration;
    }

    public void setAvgDuration(double avgDuration) {
        this.avgDuration = avgDuration;
    }

    @Override
    public String toString() {
        return "AccumulationLastAndDescentFields{" +
                "count=" + count +
                ", avgDuration=" + avgDuration +
                '}';
    }
}

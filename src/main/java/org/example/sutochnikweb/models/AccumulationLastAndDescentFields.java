package org.example.sutochnikweb.models;

public class AccumulationLastAndDescentFields {
    private Integer count;
    private String avgDuration;

    public AccumulationLastAndDescentFields() {
    }

    public AccumulationLastAndDescentFields(Integer count, String avgDuration) {
        this.count = count;
        this.avgDuration = avgDuration;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getAvgDuration() {
        return avgDuration;
    }

    public void setAvgDuration(String avgDuration) {
        this.avgDuration = avgDuration;
    }
}

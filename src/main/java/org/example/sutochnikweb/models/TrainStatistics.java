package org.example.sutochnikweb.models;

import org.springframework.stereotype.Service;
//Файлик с полями для отображения транзитных и с переработкой
@Service
public class TrainStatistics {
    private int totalTrains;
    private String avgDuration;
    private String avgWaitingDuration;
    private String avgEffectiveDuration;

    public TrainStatistics(int totalTrains, String avgDuration, String avgWaitingDuration, String avgEffectiveDuration) {
        this.totalTrains = totalTrains;
        this.avgDuration = avgDuration;
        this.avgWaitingDuration = avgWaitingDuration;
        this.avgEffectiveDuration = avgEffectiveDuration;
    }

    public int getTotalTrains() {
        return totalTrains;
    }

    public String getAvgDuration() {
        return avgDuration;
    }

    public String getAvgWaitingDuration() {
        return avgWaitingDuration;
    }

    public String getAvgEffectiveDuration() {
        return avgEffectiveDuration;
    }

    public TrainStatistics() {
    }
}

package org.example.sutochnikweb.services;

import org.example.sutochnikweb.models.AccumulationLastAndDescentFields;
import org.example.sutochnikweb.models.TrainStatistics;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Service
public class FullOvertimeStatisticService {
    private final TimeService timeService;

    public FullOvertimeStatisticService(TimeService timeService) {
        this.timeService = timeService;
    }

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");



    public TrainStatistics sumPartsOfOvertimeTrains(TrainStatistics arrivalTrainsStatistic, TrainStatistics departureTrainsStatistic, AccumulationLastAndDescentFields accumulationLastAndDescentFields) {
        TrainStatistics fullOvertimeTrainsStatistic = new TrainStatistics();

        // Суммируем общее количество поездов
        fullOvertimeTrainsStatistic.setTotalTrains(
                arrivalTrainsStatistic.getTotalTrains()
                        + departureTrainsStatistic.getTotalTrains()
                        + accumulationLastAndDescentFields.getCount()
        );

        // Суммируем среднюю продолжительность
        String totalAvgDuration = addDurations(
                arrivalTrainsStatistic.getAvgDuration(),
                departureTrainsStatistic.getAvgDuration(),
                timeService.convertMillisToTime(accumulationLastAndDescentFields.getAvgDuration())
        );
        String totalAvgWaitingDuration = addDurations(
                arrivalTrainsStatistic.getAvgWaitingDuration(),
                departureTrainsStatistic.getAvgWaitingDuration()
        );
        String totalAvgEffectiveDuration = addDurations(
                arrivalTrainsStatistic.getAvgEffectiveDuration(),
                departureTrainsStatistic.getAvgEffectiveDuration(),
                timeService.convertMillisToTime(accumulationLastAndDescentFields.getAvgDuration())
        );

        fullOvertimeTrainsStatistic.setAvgDuration(totalAvgDuration);
        fullOvertimeTrainsStatistic.setAvgWaitingDuration(totalAvgWaitingDuration);
        fullOvertimeTrainsStatistic.setAvgEffectiveDuration(totalAvgEffectiveDuration);
        return fullOvertimeTrainsStatistic;
    }

    private String addDurations(String... durations) {
        int totalSeconds = 0;

        for (String duration : durations) {
            if (duration != null && !duration.isEmpty()) {
                LocalTime time = LocalTime.parse(duration, TIME_FORMATTER);
                totalSeconds += time.toSecondOfDay();
            }
        }

        // Преобразуем общие секунды обратно в формат времени
        LocalTime totalTime = LocalTime.ofSecondOfDay(totalSeconds % (24 * 3600));
        return totalTime.format(TIME_FORMATTER);
    }
}

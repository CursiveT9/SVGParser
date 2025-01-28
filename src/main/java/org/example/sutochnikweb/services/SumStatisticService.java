package org.example.sutochnikweb.services;

import org.example.sutochnikweb.models.AccumulationLastAndDescentFields;
import org.example.sutochnikweb.models.TrainStatistics;
import org.springframework.stereotype.Service;


@Service
public class SumStatisticService {
    private final TimeService timeService;

    public SumStatisticService(TimeService timeService) {
        this.timeService = timeService;
    }

    public TrainStatistics sumPartsOfLocalTrains(TrainStatistics fullWithProcessingTrainsStatistic, TrainStatistics cargoOperationsStatistic){
        TrainStatistics localTrainsStatistic = new TrainStatistics();
        localTrainsStatistic.setTotalTrains(
                fullWithProcessingTrainsStatistic.getTotalTrains()+cargoOperationsStatistic.getTotalTrains()
        );
        String totalAvgDuration = timeService.addDurations(
                fullWithProcessingTrainsStatistic.getAvgDuration(),
                cargoOperationsStatistic.getAvgDuration()
        );
        String totalAvgWaitingDuration = timeService.addDurations(
                fullWithProcessingTrainsStatistic.getAvgWaitingDuration(),
                cargoOperationsStatistic.getAvgWaitingDuration()
        );
        String totalAvgEffectiveDuration = timeService.addDurations(
                fullWithProcessingTrainsStatistic.getAvgEffectiveDuration(),
                cargoOperationsStatistic.getAvgEffectiveDuration()
        );

        localTrainsStatistic.setAvgDuration(totalAvgDuration);
        localTrainsStatistic.setAvgWaitingDuration(totalAvgWaitingDuration);
        localTrainsStatistic.setAvgEffectiveDuration(totalAvgEffectiveDuration);
        return localTrainsStatistic;
    }

    public TrainStatistics sumPartsOfWithOvertimeTrains(TrainStatistics arrivalTrainsStatistic, TrainStatistics departureTrainsStatistic, AccumulationLastAndDescentFields accumulationLastAndDescentFields) {
        TrainStatistics fullWithProcessingTrainsStatistic = new TrainStatistics();

        // Суммируем общее количество поездов
        fullWithProcessingTrainsStatistic.setTotalTrains(
                arrivalTrainsStatistic.getTotalTrains()
                        + departureTrainsStatistic.getTotalTrains()
                        + accumulationLastAndDescentFields.getCount()
        );

        // Суммируем среднюю продолжительность
        String totalAvgDuration = timeService.addDurations(
                arrivalTrainsStatistic.getAvgDuration(),
                departureTrainsStatistic.getAvgDuration(),
                timeService.convertMillisToTime(accumulationLastAndDescentFields.getAvgDuration())

        );
        String totalAvgWaitingDuration = timeService.addDurations(
                arrivalTrainsStatistic.getAvgWaitingDuration(),
                departureTrainsStatistic.getAvgWaitingDuration()
        );
        String totalAvgEffectiveDuration = timeService.addDurations(
                arrivalTrainsStatistic.getAvgEffectiveDuration(),
                departureTrainsStatistic.getAvgEffectiveDuration()
        );

        fullWithProcessingTrainsStatistic.setAvgDuration(totalAvgDuration);
        fullWithProcessingTrainsStatistic.setAvgWaitingDuration(totalAvgWaitingDuration);
        fullWithProcessingTrainsStatistic.setAvgEffectiveDuration(totalAvgEffectiveDuration);
        return fullWithProcessingTrainsStatistic;
    }
}

package org.example.sutochnikweb.services;

import org.example.sutochnikweb.models.Action;
import org.example.sutochnikweb.models.ActionType;
import org.example.sutochnikweb.models.TrainStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
//Обработка времён транзитных и с переработкой
@Service
public class TrainStatisticsService {

    final TimeService timeService;

    public TrainStatisticsService(TimeService timeService) {
        this.timeService = timeService;
    }

    public TrainStatistics calculateTrainStatistics(Map<String, List<List<Action>>> trainMap, TimeService timeService) {
        int totalTrains = 0;
        int totalDuration = 0;
        int totalWaitingDuration = 0;
        final int TWENTY_FOUR_HOURS_IN_MILLIS = 86400000; // 24 часа в миллисекундах

        for (List<List<Action>> actionGroups : trainMap.values()) {
            for (List<Action> actions : actionGroups) {
                if (!actions.isEmpty()) {
                    totalTrains++;
                    long startTime = actions.get(0).getStart();
                    long endTime = actions.get(actions.size() - 1).getEnd();

                    // Если `endTime` меньше `startTime`, добавляем 24 часа
                    if (endTime < startTime) {
                        endTime += TWENTY_FOUR_HOURS_IN_MILLIS;
                    }

                    // Вычисляем общую длительность
                    totalDuration += endTime - startTime;

                    // Считаем общую длительность ожидания
                    totalWaitingDuration += actions.stream()
                            .filter(action -> action.getType() == ActionType.MOVEMENT_WAIT || action.getType() == ActionType.TRAIN_LOCOMOTIVE_ENTRY
                            ||action.getType() == ActionType.SLOT_WAIT || action.getType() == ActionType.IDLE_TIME
                                    || action.getType() == ActionType.CREW_WAIT)
                            .mapToInt(Action::getDuration)
                            .sum();
                }
            }
        }

        // Переводим данные во время
        String avgDuration = totalTrains > 0
                ? timeService.convertMillisToTime(totalDuration / totalTrains)
                : "";
        String avgWaitingDuration = totalTrains > 0
                ? timeService.convertMillisToTime(totalWaitingDuration / totalTrains)
                : "";
        String avgEffectiveDuration = totalTrains > 0
                ? timeService.convertMillisToTime((totalDuration - totalWaitingDuration) / totalTrains)
                : "";

        return new TrainStatistics(totalTrains, avgDuration, avgWaitingDuration, avgEffectiveDuration);
    }

    public int calculateWorkingPark(int transitWithProcessingCount, int transitWithoutProcessingCount,
                                    TrainStatistics transitWithProcessingParams, TrainStatistics transitWithoutProcessingParams){
        return (timeService.getHoursFromDuration(transitWithProcessingParams.getAvgDuration()) *
                timeService.getHoursFromDuration(transitWithoutProcessingParams.getAvgDuration())+
                transitWithProcessingCount*transitWithoutProcessingCount)/24;
    }
}

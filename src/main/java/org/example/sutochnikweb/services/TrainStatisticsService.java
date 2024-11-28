package org.example.sutochnikweb.services;

import org.example.sutochnikweb.models.Action;
import org.example.sutochnikweb.models.ActionType;
import org.example.sutochnikweb.models.TrainStatistics;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
//Обработка времён транзитных и с переработкой
@Service
public class TrainStatisticsService {
    public TrainStatistics calculateTrainStatistics(Map<String, List<List<Action>>> trainMap, TimeService timeService) {
        int totalTrains = 0;
        int totalDuration = 0;
        int totalWaitingDuration = 0;

        for (List<List<Action>> actionGroups : trainMap.values()) {
            for (List<Action> actions : actionGroups) {
                if (!actions.isEmpty()) {
                    totalTrains++;//считаем duration не как сумму операций, а как конец последнего минус начало первого
                    totalDuration += actions.get(actions.size() - 1).getEnd() - actions.get(0).getStart();
                    totalWaitingDuration += actions.stream()
                            .filter(action -> action.getType() == ActionType.MOVEMENT_WAIT || action.getType() == ActionType.TRAIN_LOCOMOTIVE_ENTRY)
                            .mapToInt(Action::getDuration)
                            .sum();
                }
            }
        }
        //Переводим во время
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
}

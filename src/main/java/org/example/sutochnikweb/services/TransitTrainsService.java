package org.example.sutochnikweb.services;


import org.example.sutochnikweb.models.Action;
import org.example.sutochnikweb.models.ActionType;
import org.example.sutochnikweb.models.HeightRange;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class TransitTrainsService {

    private final TimeService timeService;

    public TransitTrainsService(TimeService timeService) {
        this.timeService = timeService;
    }


    public void findActionPairs(Map<String, HeightRange> heightRangeMap) {
        Map<String, HeightRange> transitTrainsMap = new LinkedHashMap<>();
        for (Map.Entry<String, HeightRange> entry : heightRangeMap.entrySet()) {
            String name = entry.getKey();
            HeightRange heightRange = entry.getValue();
            List<Action> actions = heightRange.getActions();

            System.out.println("Для имени: " + name);

            List<Action> actionListAfterTrainArrival = new ArrayList<>();
            Integer startCoordinate = null;

            for (Action action : actions) {
                ActionType actionType = action.getType();

                if (actionType == ActionType.TRAIN_ARRIVAL) {
                    // Сбрасываем список при нахождении нового TRAIN_ARRIVAL
                    startCoordinate = action.getStart();
                    actionListAfterTrainArrival.clear();
                    actionListAfterTrainArrival.add(action);
                } else if (startCoordinate != null) { // Продолжаем только если установлена начальная координата
                    actionListAfterTrainArrival.add(action);

                    if (actionType == ActionType.TRAIN_DEPARTURE) {
                        // Найден TRAIN_DEPARTURE после TRAIN_ARRIVAL
                        //printActionPairDetails(startCoordinate, action.getEnd(), actionListAfterTrainArrival);
                        startCoordinate = null; // Сброс для поиска следующей пары
                    }
                    HeightRange heightRangeTransit = new HeightRange(name, actionListAfterTrainArrival);
                    transitTrainsMap.put(name, heightRangeTransit);
                }
            }
            System.out.println();
        }
        for (Map.Entry<String, HeightRange> entry : transitTrainsMap.entrySet()) {
            System.out.println(entry);
        }
    }

    // Вспомогательный метод для печати найденной пары действий
    private void printActionPairDetails(int startCoordinate, int endCoordinate, List<Action> actionsBetween) {
        System.out.println("\nНайдена пара TRAIN_ARRIVAL -> TRAIN_DEPARTURE:");
        for (Action action : actionsBetween) {
            System.out.println(action.getType() + " " + timeService.convertMillisToTime(action.getStart()) + " " + timeService.convertMillisToTime(action.getEnd()));
        }
    }

}

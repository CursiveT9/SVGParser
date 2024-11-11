package org.example.sutochnikweb.services;


import org.example.sutochnikweb.models.Action;
import org.example.sutochnikweb.models.ActionType;
import org.example.sutochnikweb.models.HeightRange;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TransitTrainsService {

    private final TimeService timeService;

    public TransitTrainsService(TimeService timeService) {
        this.timeService = timeService;
    }

    public void findActionPairs(Map<String, HeightRange> heightRangeMap) {
        // Коллекция для хранения найденных пар для каждого диапазона
        Map<String, List<List<Action>>> actionPairsMap = new LinkedHashMap<>();

        for (Map.Entry<String, HeightRange> entry : heightRangeMap.entrySet()) {
            String name = entry.getKey();
            HeightRange heightRange = entry.getValue();
            List<Action> actions = heightRange.getActions();

            // Инициализация списка для хранения пар для текущего диапазона
            List<List<Action>> pairsForCurrentRange = new ArrayList<>();
            List<Action> actionListAfterTrain_ARRIVAL = new ArrayList<>();
            boolean trainArrivalFound = false;

            for (int i = 0; i < actions.size(); i++) {
                Action currentAction = actions.get(i);

                // Начинаем новую пару, если находим TRAIN_ARRIVAL и в данный момент не ищем другую пару
                if (currentAction.getType() == ActionType.TRAIN_ARRIVAL && !trainArrivalFound) {
                    trainArrivalFound = true;
                    actionListAfterTrain_ARRIVAL.add(currentAction);
                } else if (trainArrivalFound) {
                    // Проверка на наличие элементов в actionListAfterTrain_ARRIVAL
                    if (!actionListAfterTrain_ARRIVAL.isEmpty()) {
                        Action lastAddedAction = actionListAfterTrain_ARRIVAL.get(actionListAfterTrain_ARRIVAL.size() - 1);
                        Action preLastAddedAction = actionListAfterTrain_ARRIVAL.size() > 1
                                ? actionListAfterTrain_ARRIVAL.get(actionListAfterTrain_ARRIVAL.size() - 2)
                                : null;

                        // Условие добавления следующей операции в текущую пару:
                        if (preLastAddedAction != null) {
                            // Проверяем на перекрытие с последним и предпоследним элементом
                            if (currentAction.getStart() <= lastAddedAction.getEnd() || currentAction.getStart() <= preLastAddedAction.getEnd()) {
                                actionListAfterTrain_ARRIVAL.add(currentAction);
                            } else {
                                // Начинаем новую пару, если перекрытие не найдено
                                actionListAfterTrain_ARRIVAL.clear();
                            }
                        } else {
                            // Проверяем только с последним добавленным элементом
                            if (currentAction.getStart() <= lastAddedAction.getEnd()) {
                                actionListAfterTrain_ARRIVAL.add(currentAction);
                            } else {
                                // Начинаем новую пару, если перекрытие не найдено
                                actionListAfterTrain_ARRIVAL.clear();
                            }
                        }

                        // Завершаем пару, если находим TRAIN_DEPARTURE
                        if (currentAction.getType() == ActionType.TRAIN_DEPARTURE) {
                            if(actionListAfterTrain_ARRIVAL.size()>2) {
                                pairsForCurrentRange.add(new ArrayList<>(actionListAfterTrain_ARRIVAL));
                                actionListAfterTrain_ARRIVAL.clear();
                                trainArrivalFound = false;
                            }
                        }
                    }
                }
            }

            // Добавляем список пар в общую карту, если найдены пары
            if (!pairsForCurrentRange.isEmpty()) {
                actionPairsMap.put(name, pairsForCurrentRange);
            }
        }

        // Вывод результата
        for (Map.Entry<String, List<List<Action>>> entry : actionPairsMap.entrySet()) {
            System.out.println("For name: " + entry.getKey());
            for (List<Action> actionPair : entry.getValue()) {
                System.out.println("TRAIN_ARRIVAL -> TRAIN_DEPARTURE pair found:");
                for (Action action : actionPair) {
                    System.out.println(action.getType() + " " +
                            timeService.convertMillisToTime(action.getStart()) + " " +
                            timeService.convertMillisToTime(action.getEnd()));
                }
                System.out.println();
            }
        }
    }






    // Вспомогательный метод для печати найденной пары действий
    private void printActionPairDetails(Map<String, List<List<Action>>> actionPairsMap) {
        for (Map.Entry<String, List<List<Action>>> entry : actionPairsMap.entrySet()) {
            System.out.println("For name: " + entry.getKey());
            for (List<Action> actionPair : entry.getValue()) {
                for (Action action : actionPair) {
                    System.out.println(action.getType() + " " +
                            timeService.convertMillisToTime(action.getStart()) + " " +
                            timeService.convertMillisToTime(action.getEnd()));
                }
                System.out.println();
            }
        }
    }

}

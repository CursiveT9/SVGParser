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

    public Map<String, List<List<Action>>> findTransitTrains(Map<String, HeightRange> heightRangeMap) {
        // Коллекция для хранения найденных транзитных поездов для каждого диапазона
        //Пары - действия TRAIN_ARRIVAL и TRAIN_DEPARTURE
        Map<String, List<List<Action>>> transitTrainsMap = new LinkedHashMap<>();

        for (Map.Entry<String, HeightRange> entry : heightRangeMap.entrySet()) {
            String name = entry.getKey();
            HeightRange heightRange = entry.getValue();
            List<Action> actions = heightRange.getActions();

            // Инициализация списка для хранения транзитных поездов для текущего диапазона
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
                            if (actionListAfterTrain_ARRIVAL.size() > 2) {
                                pairsForCurrentRange.add(new ArrayList<>(actionListAfterTrain_ARRIVAL));
                                actionListAfterTrain_ARRIVAL.clear();
                                trainArrivalFound = false;
                            }
                        }
                    }
                }
            }

            if (!pairsForCurrentRange.isEmpty()) {
                transitTrainsMap.put(name, pairsForCurrentRange);
            }
        }
        return transitTrainsMap;
    }
}

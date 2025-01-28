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
public class CargoOperationsService {
    public Map<String, List<List<Action>>> findCargoOperations(Map<String, HeightRange> heightRangeMap) {
    // Коллекция для хранения найденных транзитных поездов для каждого диапазона
    //Пары - действия TRAIN_ARRIVAL и TRAIN_DEPARTURE
    Map<String, List<List<Action>>> localTrainsMap = new LinkedHashMap<>();

    for (Map.Entry<String, HeightRange> entry : heightRangeMap.entrySet()) {
        String name = entry.getKey();
        HeightRange heightRange = entry.getValue();
        List<Action> actions = heightRange.getActions();

        // Инициализация списка для хранения транзитных поездов для текущего диапазона
        List<List<Action>> pairsForCurrentRange = new ArrayList<>();
        List<Action> actionListAfterTrain_ARRIVAL = new ArrayList<>();
        boolean trainArrivalFound = false;
        int max_time=0;

        for (int i = 0; i < actions.size(); i++) {
            Action currentAction = actions.get(i);
            // Начинаем новую пару, если находим SIDETRACK_PROVISION и в данный момент не ищем другую пару
            if (currentAction.getType() == ActionType.SIDETRACK_PROVISION && !trainArrivalFound) {
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
                        if (currentAction.getStart() <= lastAddedAction.getEnd() || currentAction.getStart() <= preLastAddedAction.getEnd() || currentAction.getStart()==max_time) {
                            actionListAfterTrain_ARRIVAL.add(currentAction);
                        } else {
                            // Начинаем новую пару, если перекрытие не найдено
                            actionListAfterTrain_ARRIVAL.clear();
                        }
                    } else {
                        // Проверяем только с последним добавленным элементом
                        if (currentAction.getStart() <= lastAddedAction.getEnd()||currentAction.getType()==ActionType.LOADING) {
                            actionListAfterTrain_ARRIVAL.add(currentAction);
                        } else {
                            // Начинаем новую пару, если перекрытие не найдено
                            actionListAfterTrain_ARRIVAL.clear();
                        }
                    }
                    if(currentAction.getEnd()>max_time){
                        max_time=currentAction.getEnd();
                    }

                    // Завершаем пару, если находим TRAIN_DEPARTURE
                    if (currentAction.getType() == ActionType.SIDETRACK_CLEANING ||
                            ((currentAction.getType()==ActionType.LOADING||currentAction.getType()==ActionType.UNLOADING||
                                    currentAction.getType()==ActionType.MOVEMENT_WAIT||
                                    currentAction.getType()==ActionType.TRAIN_LOCOMOTIVE_ENTRY||
                                    currentAction.getType()==ActionType.SLOT_WAIT||
                                    currentAction.getType()==ActionType.CREW_WAIT||
                                    currentAction.getType()==ActionType.DISSOLUTION_PERMISSION_WAIT
                                    )
                                    &&(currentAction.getEnd() == 43200000 || currentAction.getEnd() == 86400000))) {
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
            localTrainsMap.put(name, pairsForCurrentRange);
        }
    }
    for (Map.Entry<String, List<List<Action>>> entry : localTrainsMap.entrySet()) {
        String path = entry.getKey(); // Путь (ключ)
        List<List<Action>> actionsLists = entry.getValue(); // Список действий

        System.out.println("Путь: " + path);
        for (List<Action> actions : actionsLists) {
            for (Action action : actions) {
                System.out.println("    " + action); // Отображение каждого действия
            }
        }
        System.out.println(); // Отступ между путями
    }
    return localTrainsMap;
    }
}


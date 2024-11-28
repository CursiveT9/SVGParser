package org.example.sutochnikweb.services;

import org.example.sutochnikweb.models.Action;
import org.example.sutochnikweb.models.ActionType;
import org.example.sutochnikweb.models.HeightRange;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TrainsWithOvertimeService {

    private final TimeService timeService;

    public TrainsWithOvertimeService(TimeService timeService) {
        this.timeService = timeService;
    }

    public Map<String, List<List<Action>>> findTrainsWithOvertime(Map<String, HeightRange> heightRangeMap) {
        Map<String, List<List<Action>>> reprocessedTrainsMap = new LinkedHashMap<>();

        for (Map.Entry<String, HeightRange> entry : heightRangeMap.entrySet()) {
            String name = entry.getKey();
            HeightRange heightRange = entry.getValue();
            List<Action> actions = heightRange.getActions();

            List<List<Action>> sequences = new ArrayList<>();
            List<Action> currentSequence = new ArrayList<>();
            boolean sequenceStarted = false;
            boolean containsMandatoryAction = false;
            boolean containsInspectionStop = false;
            boolean parralelAction = false;
            boolean containsTrainDeparture = false;

            for (int i = 0; i < actions.size(); i++) {
                Action action = actions.get(i);
                if (!action.getCompleted()) {
                    sequenceStarted = false;
                    continue;
                }
                // Если действие — TRAIN_ARRIVAL, начинаем новую последовательность
                if (action.getType() == ActionType.TRAIN_ARRIVAL) {
                    sequenceStarted = true;
                    currentSequence = new ArrayList<>();
                    containsMandatoryAction = false;
                    containsInspectionStop = false;
                    containsTrainDeparture = false;
                    parralelAction = false;
                    currentSequence.add(action);
                } else if (sequenceStarted) {
                    currentSequence.add(action);

                    // Проверяем наличие обязательных типов действий
                    if (action.getType() == ActionType.SHUNTING_LOCOMOTIVE_ATTACHMENT ||
                            action.getType() == ActionType.TRAIN_LOCOMOTIVE_ATTACHMENT ||
                            action.getType() == ActionType.HUMP_LOCOMOTIVE_ATTACHMENT) {
                        containsMandatoryAction = true;
                    }

                    if (action.getType() == ActionType.TRAIN_INSPECTION_STOP) {
                        containsInspectionStop = true;
                    }
                    if(action.getType()==ActionType.TRAIN_DEPARTURE){
                        containsTrainDeparture = true;
                    }

                    // Проверяем временной разрыв или конец списка
                    if (i < actions.size() - 1) {
                        Action nextAction = actions.get(i + 1);
                        long gap = nextAction.getStart() - action.getEnd();
                        if ((gap < 0) && (gap > -20000000)) { // Параллельное действие // неверно посчитает если следующая операция начнется в следующий день и будет более чем через 5.55 часов
                            parralelAction = true;
                        } else if (parralelAction) {
                            parralelAction = false;
                        } else if ((gap > 60000) || ((gap < 0) && (gap + 86400000 > 60000))) { // если между операциями больше чем 1 минута в миллисекундах, то считаем что закончилось
                            sequenceStarted = false;
                            if (containsMandatoryAction && containsInspectionStop && !containsTrainDeparture) {
                                sequences.add(new ArrayList<>(currentSequence));
                            }
                        }
                    } else {
                        // Если конец списка, завершаем последовательность
                        sequenceStarted = false;
                        if (containsMandatoryAction && containsInspectionStop && !containsTrainDeparture) {
                            sequences.add(new ArrayList<>(currentSequence));
                        }
                    }
                }
            }

            reprocessedTrainsMap.put(name, sequences);
        }

        // Вывод результатов
        //printActionPairDetails(reprocessedTrainsMap);
        return reprocessedTrainsMap;
    }

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

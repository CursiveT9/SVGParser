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
        List<Action> actionListAfterTrain_ARRIVAL = new ArrayList<>();
        for (Map.Entry<String, HeightRange> entry : heightRangeMap.entrySet()) {
            String name = entry.getKey();
            HeightRange heightRange = entry.getValue();
            List<Action> actions = heightRange.getActions();

            System.out.println("For name: " + name);

            for (int i = 0; i < actions.size(); i++) {
                Action currentAction = actions.get(i);

                if (currentAction.getType() == ActionType.TRAIN_ARRIVAL) {
                    int startCoordinate = currentAction.getStart();

                    for (int j = i + 1; j < actions.size(); j++) {
                        Action currentInnerAction = actions.get(j-1);
                        Action nextInnerAction = actions.get(j);

                        if(currentInnerAction.getEnd()==nextInnerAction.getStart()) {
                            actionListAfterTrain_ARRIVAL.add(nextInnerAction);
                        }
                        if(nextInnerAction.getType() == ActionType.TRAIN_ARRIVAL){
                            startCoordinate = nextInnerAction.getStart();
                            actionListAfterTrain_ARRIVAL.clear();
                        }
                        if (nextInnerAction.getType() == ActionType.TRAIN_DEPARTURE) {//Записывать в список, потом сохранять если есть train_departure
                            int endCoordinate = nextInnerAction.getEnd();
                            System.out.println();
                            System.out.println("TRAIN_ARRIVAL -> TRAIN_DEPARTURE pair found:");
                            for (Action action: actionListAfterTrain_ARRIVAL) {
                                System.out.println(action.getType() +" "+ timeService.convertMillisToTime(action.getStart())+ " "+ timeService.convertMillisToTime(action.getEnd()));
                            }
                            System.out.println("Start Coordinate (TRAIN_ARRIVAL): " + timeService.convertMillisToTime(startCoordinate));
                            System.out.println("End Coordinate (TRAIN_DEPARTURE): " + timeService.convertMillisToTime(endCoordinate));
                            break;
                        }
                    }
                    actionListAfterTrain_ARRIVAL.clear();
                }
            }
            System.out.println();
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

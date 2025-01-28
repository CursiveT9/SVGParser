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
    // ��������� ��� �������� ��������� ���������� ������� ��� ������� ���������
    //���� - �������� TRAIN_ARRIVAL � TRAIN_DEPARTURE
    Map<String, List<List<Action>>> localTrainsMap = new LinkedHashMap<>();

    for (Map.Entry<String, HeightRange> entry : heightRangeMap.entrySet()) {
        String name = entry.getKey();
        HeightRange heightRange = entry.getValue();
        List<Action> actions = heightRange.getActions();

        // ������������� ������ ��� �������� ���������� ������� ��� �������� ���������
        List<List<Action>> pairsForCurrentRange = new ArrayList<>();
        List<Action> actionListAfterTrain_ARRIVAL = new ArrayList<>();
        boolean trainArrivalFound = false;
        int max_time=0;

        for (int i = 0; i < actions.size(); i++) {
            Action currentAction = actions.get(i);
            // �������� ����� ����, ���� ������� SIDETRACK_PROVISION � � ������ ������ �� ���� ������ ����
            if (currentAction.getType() == ActionType.SIDETRACK_PROVISION && !trainArrivalFound) {
                trainArrivalFound = true;
                actionListAfterTrain_ARRIVAL.add(currentAction);
            } else if (trainArrivalFound) {
                // �������� �� ������� ��������� � actionListAfterTrain_ARRIVAL
                if (!actionListAfterTrain_ARRIVAL.isEmpty()) {
                    Action lastAddedAction = actionListAfterTrain_ARRIVAL.get(actionListAfterTrain_ARRIVAL.size() - 1);
                    Action preLastAddedAction = actionListAfterTrain_ARRIVAL.size() > 1
                            ? actionListAfterTrain_ARRIVAL.get(actionListAfterTrain_ARRIVAL.size() - 2)
                            : null;

                    // ������� ���������� ��������� �������� � ������� ����:
                    if (preLastAddedAction != null) {
                        // ��������� �� ���������� � ��������� � ������������� ���������
                        if (currentAction.getStart() <= lastAddedAction.getEnd() || currentAction.getStart() <= preLastAddedAction.getEnd() || currentAction.getStart()==max_time) {
                            actionListAfterTrain_ARRIVAL.add(currentAction);
                        } else {
                            // �������� ����� ����, ���� ���������� �� �������
                            actionListAfterTrain_ARRIVAL.clear();
                        }
                    } else {
                        // ��������� ������ � ��������� ����������� ���������
                        if (currentAction.getStart() <= lastAddedAction.getEnd()||currentAction.getType()==ActionType.LOADING) {
                            actionListAfterTrain_ARRIVAL.add(currentAction);
                        } else {
                            // �������� ����� ����, ���� ���������� �� �������
                            actionListAfterTrain_ARRIVAL.clear();
                        }
                    }
                    if(currentAction.getEnd()>max_time){
                        max_time=currentAction.getEnd();
                    }

                    // ��������� ����, ���� ������� TRAIN_DEPARTURE
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
        String path = entry.getKey(); // ���� (����)
        List<List<Action>> actionsLists = entry.getValue(); // ������ ��������

        System.out.println("����: " + path);
        for (List<Action> actions : actionsLists) {
            for (Action action : actions) {
                System.out.println("    " + action); // ����������� ������� ��������
            }
        }
        System.out.println(); // ������ ����� ������
    }
    return localTrainsMap;
    }
}


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
public class DepartureService {

    public Map<String, List<List<Action>>> findFormationOrShuntingPairs(Map<String, HeightRange> heightRangeMap) {
        // ��������� ��� �������� ��������� ��� FORMATION_COMPLETION, SHUNTING � ������������ TRAIN_DEPARTURE
        Map<String, List<List<Action>>> formationOrShuntingMap = new LinkedHashMap<>();

        for (Map.Entry<String, HeightRange> entry : heightRangeMap.entrySet()) {
            String name = entry.getKey();
            HeightRange heightRange = entry.getValue();
            List<Action> actions = heightRange.getActions();

            // ������ ��� �������� ��� ��� �������� ���������
            List<List<Action>> pairsForCurrentRange = new ArrayList<>();
            List<Action> currentPair = new ArrayList<>();
            boolean pairStarted = false;

            for (int i = 0; i < actions.size(); i++) {
                Action currentAction = actions.get(i);

                // ��������� ������� TRAIN_ARRIVAL
                if (currentAction.getType() == ActionType.TRAIN_ARRIVAL) {
                    // ��������� ������� ���� � ��������� ��������
                    currentPair.clear();
                    pairStarted = false;
                    continue;
                }

                // �������� ����� ����, ���� ��� FORMATION_COMPLETION ��� SHUNTING
                if ((currentAction.getType() == ActionType.FORMATION_COMPLETION || currentAction.getType() == ActionType.SHUNTING)
                        && !pairStarted) {
                    pairStarted = true;
                    currentPair.clear();
                    currentPair.add(currentAction);
                } else if (pairStarted) {
                    // ��������� ������������������ ��������
                    Action lastAction = currentPair.get(currentPair.size() - 1);

                    // ���� ������ �������� �������� ����� ����� ���������� � ����
                    // ��� ������ �������� ����� ���� ����� ����� ����������
                    boolean isSequential = currentAction.getStart()<=(lastAction.getEnd()) ||
                            currentPair.size() >= 2 &&currentAction.getStart()<=(currentPair.get(currentPair.size()-2).getEnd()) ||
                            currentPair.size() >= 3 &&currentAction.getStart()<=(currentPair.get(currentPair.size()-3).getEnd()) ||
                            currentPair.size() >= 4 &&currentAction.getStart()<=(currentPair.get(currentPair.size()-4).getEnd()) ||
                            currentPair.size() >= 5 &&currentAction.getStart()<=(currentPair.get(currentPair.size()-5).getEnd()) ||
                            currentPair.size() >= 6 &&currentAction.getStart()<=(currentPair.get(currentPair.size()-6).getEnd()) ||
                            (i + 1 < actions.size() && actions.get(i + 1).getStart()<=(lastAction.getEnd()))||
                            (i + 2 < actions.size() && actions.get(i + 2).getStart()<=(lastAction.getEnd()))||
                            (i + 3 < actions.size() && actions.get(i + 3).getStart()<=(lastAction.getEnd()))||
                            (i + 4 < actions.size() && actions.get(i + 4).getStart()<=(lastAction.getEnd()))||
                            (i + 5 < actions.size() && actions.get(i + 5).getStart()<=(lastAction.getEnd()))||
                            (i + 6 < actions.size() && actions.get(i + 6).getStart()<=(lastAction.getEnd()));

                    if (isSequential) {
                        currentPair.add(currentAction);

                        // ��������� ���� ��� ���������� TRAIN_DEPARTURE
                        if (currentAction.getType() == ActionType.TRAIN_DEPARTURE && currentPair.size() > 3) {
                            pairsForCurrentRange.add(new ArrayList<>(currentPair));
                            currentPair.clear();
                            pairStarted = false;
                        }
                    } else {
                        // ���� ������������������ ��������, ���������� ������� ����
                        currentPair.clear();
                        pairStarted = false;
                    }
                }
            }

            // ���� ������� ����, ��������� ��
            if (!pairsForCurrentRange.isEmpty()) {
                formationOrShuntingMap.put(name, pairsForCurrentRange);
            }
        }

        // ������ ����������
        //printFormationOrShuntingMap(formationOrShuntingMap);
        return formationOrShuntingMap;

    }

   /* public Map<String, List<String>> getPairsDurations(Map<String, List<List<Action>>> formationOrShuntingMap) {
        Map<String, List<String>> pairsTimeMap = new LinkedHashMap<>();

        for (Map.Entry<String, List<List<Action>>> entry : formationOrShuntingMap.entrySet()) {
            String name = entry.getKey();
            List<List<Action>> pairs = entry.getValue();
            List<String> pairDurations = new ArrayList<>();

            for (List<Action> pair : pairs) {
                if (!pair.isEmpty()) {
                    long startTime = pair.get(0).getStart(); // ������ ������� ��������
                    long endTime = pair.get(pair.size() - 1).getEnd(); // ����� ���������� ��������
                    if(startTime>endTime){
                        endTime+=86400000;
                    }
                    long duration = endTime - startTime; // ������� (������������)
                    pairDurations.add("Duration: " + duration + " ms" +" Start "+ startTime+ " End " + endTime);
                }
            }
            pairsTimeMap.put(name, pairDurations);
        }
        return pairsTimeMap;
    }
*/

    private void printFormationOrShuntingMap(Map<String, List<List<Action>>> map) {
        for (Map.Entry<String, List<List<Action>>> entry : map.entrySet()) {
            System.out.println("Height Range: " + entry.getKey());
            for (List<Action> pair : entry.getValue()) {
                System.out.println("Pair:");
                for (Action action : pair) {
                    System.out.println("  Action Type: " + action.getType() + ", Start: " + action.getStart() + ", End: " + action.getEnd());
                }
            }
        }
    }
}

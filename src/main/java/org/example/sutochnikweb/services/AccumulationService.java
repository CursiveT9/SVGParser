package org.example.sutochnikweb.services;

import org.example.sutochnikweb.models.AccumulationLastAndDescentFields;
import org.example.sutochnikweb.models.Action;
import org.example.sutochnikweb.models.ActionType;
import org.example.sutochnikweb.models.HeightRange;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AccumulationService {
    private final TimeService timeService;
    private final SVGService svgService;

    public AccumulationService(TimeService timeService, SVGService svgService) {
        this.timeService = timeService;
        this.svgService = svgService;
    }


    public AccumulationLastAndDescentFields findAverageAccumulationDuration(Map<String, HeightRange> heightRangeMap) {
        // Map ��� �������� ������� ����������������� ������������������� ACCUMULATION ��� ������� ����
        Map<String, AccumulationLastAndDescentFields> averageDurationMap = new LinkedHashMap<>();
        AccumulationLastAndDescentFields accumulationLastAndDescentFields = new AccumulationLastAndDescentFields();
        int totalAccumulationCount = 0;
        double totalAverageDuration = 0;
        Set<HeightRange> accumulationRanges=new HashSet<>();
        // �������� �� ������� �������� �� heightRangeMap
        for (Map.Entry<String, HeightRange> entry : heightRangeMap.entrySet()) {

            String name = entry.getKey(); // ��� ����
            HeightRange heightRange = entry.getValue(); // �������� �����
            List<Action> actions = heightRange.getActions(); // ������ ��������

            List<Long> durations = new ArrayList<>(); // ������ ��� �������� ������������ ������ ������������������
            List<Action> accumulationSequence = new ArrayList<>(); // ������ ��� ������������ ������� ������������������ ACCUMULATION// �������� �� ���������
            for (int i = 0; i < actions.size(); i++) {
                Action currentAction = actions.get(i);
                // ���� ������� ������� - ��� ACCUMULATION, ��������� ��� � ������������������
                if (currentAction.getType() == ActionType.ACCUMULATION) {
                    // ���� ������������������ ������, �������� �����
                    if (accumulationSequence.isEmpty()) {
                        accumulationSequence.add(currentAction);
                    } else {
                        // ���� ������ ������ ACCUMULATION ��������� � ������ �����������, ���������� ������� ������������������
                        if (currentAction.getStart() == accumulationSequence.get(accumulationSequence.size() - 1).getEnd()) {
                            accumulationSequence.add(currentAction);
                        } else {
                            // �����, �������� ����� ������������������
                            accumulationSequence.clear();
                            accumulationSequence.add(currentAction);
                        }
                    }
                    Action shunting = findNextAction(actions, i, ActionType.SHUNTING);
                    if(shunting!=null){
                        Action shuntingLocomotiveAttachment = findNextAction(actions, actions.indexOf(shunting), ActionType.SHUNTING_LOCOMOTIVE_ATTACHMENT);
                        if(shuntingLocomotiveAttachment!=null&&currentAction.getEnd() == shuntingLocomotiveAttachment.getStart()){
                            Action formationCompletion = findNextAction(actions,actions.indexOf(shuntingLocomotiveAttachment), ActionType.FORMATION_COMPLETION);
                            if (formationCompletion != null) {
                                long adjustedEndTime = formationCompletion.getStart();
                                // ��������� ������� ����� �����
                                long duration;
                                // ���������� ����������������� ������������������
                                if (accumulationSequence.get(0).getStart() > adjustedEndTime) {
                                    adjustedEndTime += 24 * 60 * 60 * 1000; // ��������� ����� � �������������
                                }
                                duration = adjustedEndTime - accumulationSequence.get(0).getStart();
                                durations.add(duration);
                                accumulationRanges.add(heightRange);
                                // �������� ������� ������������������, ��� ��� ��� ���������
                            }
                        }
                    }
                    else {
                        // ���������, ���� �� ��� ������� ������������������ SHUNTING_LOCOMOTIVE_ATTACHMENT � FORMATION_COMPLETION
                        Action shuntingLocomotiveAttachment = findNextAction(actions, i, ActionType.SHUNTING_LOCOMOTIVE_ATTACHMENT);
                        if (shuntingLocomotiveAttachment != null
                                && shuntingLocomotiveAttachment.getStart() == currentAction.getStart()) {
                            Action formationCompletion = findNextAction(actions,
                                    actions.indexOf(shuntingLocomotiveAttachment),
                                    ActionType.FORMATION_COMPLETION);

                            // ���� ������������������ ���������, ��������� ������������
                            if (formationCompletion != null) {
                                long adjustedEndTime = formationCompletion.getStart();
                                // ��������� ������� ����� �����
                                long duration;
                                // ���������� ����������������� ������������������
                                if (accumulationSequence.get(0).getStart() > adjustedEndTime) {
                                    adjustedEndTime += 24 * 60 * 60 * 1000; // ��������� ����� � �������������
                                }
                                duration = adjustedEndTime - accumulationSequence.get(0).getStart();
                                durations.add(duration);
                                accumulationRanges.add(heightRange);
                                // �������� ������� ������������������, ��� ��� ��� ���������
                            }
                        }
                    }
                }
            }

            // ���� ���� ������� ������������������, ��������� ������� �����������������
            if (!durations.isEmpty()) {
                long totalDuration = durations.stream().mapToLong(Long::longValue).sum();
                double averageDuration = (double) totalDuration / durations.size();
                totalAccumulationCount+=durations.size();
                totalAverageDuration+=totalDuration;
                System.out.println("Range: " + heightRange.getName());
                System.out.println("Durations: " + durations);
                System.out.println("Total Duration: " + totalDuration);
            }
        }
        //37 ��� ���� �������
        accumulationLastAndDescentFields.setCount(totalAccumulationCount);
        accumulationLastAndDescentFields.setAvgDuration(
                totalAccumulationCount > 0 ? totalAverageDuration/totalAccumulationCount : 0
        );
        System.out.println("Total Accumulation Count: "+totalAccumulationCount);
        System.out.println("Total Average Duration: " +totalAverageDuration);
        System.out.println("Total 1/2: "+totalAverageDuration/accumulationRanges.size());
        System.out.println(accumulationRanges.size());
        return accumulationLastAndDescentFields; // ���������� map � ������������
    }

    public AccumulationLastAndDescentFields findEndAccumulationSequences(Map<String, HeightRange> heightRangeMap) {
        Map<String, String> endAccumulationMap = new LinkedHashMap<>();
        AccumulationLastAndDescentFields accumulationLastAndDescentFields = new AccumulationLastAndDescentFields();
        int totalAccumulationCount = 0;
        double totalAverageDuration = 0;
        Set<HeightRange> accumulationRanges=new HashSet<>();
        for (Map.Entry<String, HeightRange> entry : heightRangeMap.entrySet()) {
            String name = entry.getKey();
            HeightRange heightRange = entry.getValue();
            List<Action> actions = heightRange.getActions();

            long totalDuration = 0;
            int count = 0;

            // ���������� � ����� �� ���� ���������
            for (int i = actions.size() - 1; i >= 0; i--) {
                Action currentAction = actions.get(i);

                // ���� ������������������ ACCUMULATION, ��������������� �� ����� �������
                // TODO :  currentAction.getEnd() == 43200000 �� ������ ����� �� 12 �����, ����� ���� �� ����� ����� � 5 � 16 ���
                if(actions.getLast().getType()==ActionType.ACCUMULATION) {
                    if (currentAction.getType() == ActionType.ACCUMULATION && (currentAction.getEnd() == 43200000 || currentAction.getEnd() == 86400000)) {
                        int sequenceStartIndex = i;
                        long durationSum = currentAction.getDuration();

                        // ���������� ������ ������ ������������������
                        for (int j = i - 1; j >= 0; j--) {
                            Action previousAction = actions.get(j);

                            if (previousAction.getType() == ActionType.ACCUMULATION) {
                                // ���� ��� ACCUMULATION, ���������, �������� �� ��� ���������� ������������������
                                if (isNextInSequence(previousAction, actions.get(sequenceStartIndex))) {
                                    durationSum += previousAction.getDuration();
                                    sequenceStartIndex = j; // ��������� ������ ������ ������������������
                                } else {
                                    break; // ���� ������������������ ���������, ��������� �����
                                }
                            } else {
                                // ���������� ��-ACCUMULATION ��������, ���� ��� �� �������� �������
                                if (!isActionBreakingSequence(previousAction, actions.get(sequenceStartIndex))) {
                                    continue;
                                } else {
                                    break; // ���������, ���� �������� �������� ������������������
                                }
                            }
                        }

                        // ���������, ���� �� ������ �������� � ������������������
                        boolean isValidSequence = true;
                        for (int k = sequenceStartIndex; k <= i; k++) {
                            if (actions.get(k).getType() != ActionType.ACCUMULATION) {
                                isValidSequence = false;
                                break;
                            }
                        }

                        // ���� ������������������ ���������, ���������� �
                        if (isValidSequence) {
                            totalDuration += durationSum;
                            count++;
                            break; // ��������� ���������, ��� ��� ����� �������� ������������������
                        }
                    }
                }
            }

            // ��������� ������� �����������������, ���� ������� ������������������
            if (count > 0) {
                double averageDuration = (double) totalDuration / count;
                totalAccumulationCount+=count;
                totalAverageDuration+=averageDuration;
                accumulationRanges.add(heightRange);
                System.out.println("Range: " + heightRange.getName());
                System.out.println("Durations: " + averageDuration);
                System.out.println("Total Duration: " + totalDuration);                //endAccumulationMap.put(name, stringAverageDuration);
            }
        }

        accumulationLastAndDescentFields.setCount(totalAccumulationCount);
        accumulationLastAndDescentFields.setAvgDuration(
                totalAccumulationCount > 0 ? totalAverageDuration/totalAccumulationCount : 0
        );
        return accumulationLastAndDescentFields; // ���������� map � ������������
    }

    // �������� ������������ ������������������ � ������ �������� ����� �����
    private boolean isNextInSequence(Action previousAction, Action currentAction) {
        long previousEnd = previousAction.getEnd();
        long currentStart = currentAction.getStart();

        if (previousEnd == currentStart) {
            return true; // ������� ������
        }

        // ������� ����� �����
        if (currentStart < previousEnd) {
            long adjustedCurrentStart = currentStart + 86400000; // ��������� 24 ����
            return previousEnd == adjustedCurrentStart;
        }

        return false; // ���� �� ���������, ������������������ ���������
    }

    // ��������, �������� �� �������� ������������������
    private boolean isActionBreakingSequence(Action action, Action referenceAction) {
        // ���� �������� �� ������ �� ������� ������������������, ��� �� ��������
        if (action.getType() != ActionType.ACCUMULATION) {
            // ���������, ��������� �� �������� ��������� ��� ��������� ������� ������������������
            return action.getEnd() > referenceAction.getStart() && action.getStart() < referenceAction.getEnd();
        }
        // ��� ACCUMULATION ��������� ������ �������� ����� �����
        return false; // ACCUMULATION �� �������� ������������������
    }

    private Action findNextAction(List<Action> actions, int startIndex, ActionType targetType) {
        for (int i = startIndex + 1; i < actions.size(); i++) {
            Action action = actions.get(i);
            if (action.getType() == targetType) {
                return action;
            }
        }
        return null;
    }
}

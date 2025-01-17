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
        // Map для хранения средней продолжительности последовательностей ACCUMULATION для каждого пути
        Map<String, AccumulationLastAndDescentFields> averageDurationMap = new LinkedHashMap<>();
        AccumulationLastAndDescentFields accumulationLastAndDescentFields = new AccumulationLastAndDescentFields();
        int totalAccumulationCount = 0;
        double totalAverageDuration = 0;
        Set<HeightRange> accumulationRanges=new HashSet<>();
        // Проходим по каждому элементу из heightRangeMap
        for (Map.Entry<String, HeightRange> entry : heightRangeMap.entrySet()) {

            String name = entry.getKey(); // Имя пути
            HeightRange heightRange = entry.getValue(); // Диапазон высот
            List<Action> actions = heightRange.getActions(); // Список действий

            List<Long> durations = new ArrayList<>(); // Список для хранения длительности каждой последовательности
            List<Action> accumulationSequence = new ArrayList<>(); // Список для отслеживания текущей последовательности ACCUMULATION// Проходим по действиям
            for (int i = 0; i < actions.size(); i++) {
                Action currentAction = actions.get(i);
                // Если текущий элемент - это ACCUMULATION, добавляем его в последовательность
                if (currentAction.getType() == ActionType.ACCUMULATION) {
                    // Если последовательность пустая, начинаем новую
                    if (accumulationSequence.isEmpty()) {
                        accumulationSequence.add(currentAction);
                    } else {
                        // Если начало нового ACCUMULATION совпадает с концом предыдущего, продолжаем текущую последовательность
                        if (currentAction.getStart() == accumulationSequence.get(accumulationSequence.size() - 1).getEnd()) {
                            accumulationSequence.add(currentAction);
                        } else {
                            // Иначе, начинаем новую последовательность
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
                                // Учитываем переход через сутки
                                long duration;
                                // Записываем продолжительность последовательности
                                if (accumulationSequence.get(0).getStart() > adjustedEndTime) {
                                    adjustedEndTime += 24 * 60 * 60 * 1000; // Добавляем сутки в миллисекундах
                                }
                                duration = adjustedEndTime - accumulationSequence.get(0).getStart();
                                durations.add(duration);
                                accumulationRanges.add(heightRange);
                                // Очистить текущую последовательность, так как она завершена
                            }
                        }
                    }
                    else {
                        // Проверяем, есть ли для текущей последовательности SHUNTING_LOCOMOTIVE_ATTACHMENT и FORMATION_COMPLETION
                        Action shuntingLocomotiveAttachment = findNextAction(actions, i, ActionType.SHUNTING_LOCOMOTIVE_ATTACHMENT);
                        if (shuntingLocomotiveAttachment != null
                                && shuntingLocomotiveAttachment.getStart() == currentAction.getStart()) {
                            Action formationCompletion = findNextAction(actions,
                                    actions.indexOf(shuntingLocomotiveAttachment),
                                    ActionType.FORMATION_COMPLETION);

                            // Если последовательность завершена, вычисляем длительность
                            if (formationCompletion != null) {
                                long adjustedEndTime = formationCompletion.getStart();
                                // Учитываем переход через сутки
                                long duration;
                                // Записываем продолжительность последовательности
                                if (accumulationSequence.get(0).getStart() > adjustedEndTime) {
                                    adjustedEndTime += 24 * 60 * 60 * 1000; // Добавляем сутки в миллисекундах
                                }
                                duration = adjustedEndTime - accumulationSequence.get(0).getStart();
                                durations.add(duration);
                                accumulationRanges.add(heightRange);
                                // Очистить текущую последовательность, так как она завершена
                            }
                        }
                    }
                }
            }

            // Если были найдены последовательности, вычисляем среднюю продолжительность
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
        //37 два раза считает
        accumulationLastAndDescentFields.setCount(totalAccumulationCount);
        accumulationLastAndDescentFields.setAvgDuration(
                totalAccumulationCount > 0 ? totalAverageDuration/totalAccumulationCount : 0
        );
        System.out.println("Total Accumulation Count: "+totalAccumulationCount);
        System.out.println("Total Average Duration: " +totalAverageDuration);
        System.out.println("Total 1/2: "+totalAverageDuration/accumulationRanges.size());
        System.out.println(accumulationRanges.size());
        return accumulationLastAndDescentFields; // Возвращаем map с результатами
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

            // Проходимся с конца по всем действиям
            for (int i = actions.size() - 1; i >= 0; i--) {
                Action currentAction = actions.get(i);

                // Ищем последовательность ACCUMULATION, заканчивающуюся на конце графика
                // TODO :  currentAction.getEnd() == 43200000 Не всегда конец на 12 часах, может быть на любом место и 5 и 16 итд
                if(actions.getLast().getType()==ActionType.ACCUMULATION) {
                    if (currentAction.getType() == ActionType.ACCUMULATION && (currentAction.getEnd() == 43200000 || currentAction.getEnd() == 86400000)) {
                        int sequenceStartIndex = i;
                        long durationSum = currentAction.getDuration();

                        // Продолжаем искать начало последовательности
                        for (int j = i - 1; j >= 0; j--) {
                            Action previousAction = actions.get(j);

                            if (previousAction.getType() == ActionType.ACCUMULATION) {
                                // Если это ACCUMULATION, проверяем, образует ли оно корректную последовательность
                                if (isNextInSequence(previousAction, actions.get(sequenceStartIndex))) {
                                    durationSum += previousAction.getDuration();
                                    sequenceStartIndex = j; // Обновляем индекс начала последовательности
                                } else {
                                    break; // Если последовательность разорвана, завершаем поиск
                                }
                            } else {
                                // Пропускаем не-ACCUMULATION действия, если они не нарушают порядок
                                if (!isActionBreakingSequence(previousAction, actions.get(sequenceStartIndex))) {
                                    continue;
                                } else {
                                    break; // Прерываем, если действие нарушает последовательность
                                }
                            }
                        }

                        // Проверяем, есть ли другие действия в последовательности
                        boolean isValidSequence = true;
                        for (int k = sequenceStartIndex; k <= i; k++) {
                            if (actions.get(k).getType() != ActionType.ACCUMULATION) {
                                isValidSequence = false;
                                break;
                            }
                        }

                        // Если последовательность корректна, записываем её
                        if (isValidSequence) {
                            totalDuration += durationSum;
                            count++;
                            break; // Завершаем обработку, так как нашли валидную последовательность
                        }
                    }
                }
            }

            // Вычисляем среднюю продолжительность, если найдены последовательности
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
        return accumulationLastAndDescentFields; // Возвращаем map с результатами
    }

    // Проверка корректности последовательности с учетом перехода через сутки
    private boolean isNextInSequence(Action previousAction, Action currentAction) {
        long previousEnd = previousAction.getEnd();
        long currentStart = currentAction.getStart();

        if (previousEnd == currentStart) {
            return true; // Обычный случай
        }

        // Переход через сутки
        if (currentStart < previousEnd) {
            long adjustedCurrentStart = currentStart + 86400000; // Добавляем 24 часа
            return previousEnd == adjustedCurrentStart;
        }

        return false; // Если не совпадает, последовательность разорвана
    }

    // Проверка, нарушает ли действие последовательность
    private boolean isActionBreakingSequence(Action action, Action referenceAction) {
        // Если действие не влияет на текущую последовательность, оно не нарушает
        if (action.getType() != ActionType.ACCUMULATION) {
            // Проверяем, находится ли действие полностью вне диапазона текущей последовательности
            return action.getEnd() > referenceAction.getStart() && action.getStart() < referenceAction.getEnd();
        }
        // Для ACCUMULATION проверяем логику перехода через сутки
        return false; // ACCUMULATION не нарушает последовательность
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

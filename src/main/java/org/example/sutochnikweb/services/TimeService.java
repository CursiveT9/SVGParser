package org.example.sutochnikweb.services;

import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Service
public class TimeService {
    public String convertMillisToTime(int millis){
        int seconds = millis / 1000;

        // Извлекаем количество часов
        int hours = seconds / 3600;

        // Извлекаем оставшиеся минуты
        int minutes = (seconds % 3600) / 60;

        // Извлекаем оставшиеся секунды
        long remainingSeconds = seconds % 60;

        // Форматируем в строку вида "HH:mm:ss"
        return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
    }

    public String convertMillisToTime(double millis){
        int seconds = (int) (millis / 1000);

        // Извлекаем количество часов
        int hours = seconds / 3600;

        // Извлекаем оставшиеся минуты
        int minutes = (seconds % 3600) / 60;

        // Извлекаем оставшиеся секунды
        long remainingSeconds = seconds % 60;

        // Форматируем в строку вида "HH:mm:ss"
        return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
    }

    public double convertTimeToMillis(String timeString) {
        // Разбиваем строку на часы, минуты и секунды
        String[] parts = timeString.split(":");

        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        int seconds = Integer.parseInt(parts[2]);

        // Преобразуем всё в миллисекунды
        return (hours * 3600 + minutes * 60 + seconds) * 1000.0;
    }


    public int calculateMillisFromHours(int startTime){
        return startTime*60*60*1000;
    }

    public double getHoursFromDuration(String duration) {
        if (duration != null && !duration.isEmpty()) {
            String[] parts = duration.split(":"); // Разделяем строку по символу ':'
            if (parts.length == 3) {
                int hours = Integer.parseInt(parts[0]); // Извлекаем часы
                int minutes = Integer.parseInt(parts[1]); // Извлекаем минуты
                // Преобразуем минуты в доли часа и складываем
                return hours + (minutes / 60.0);
            }
        }
        return 0.0; // Значение по умолчанию
    }
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    public String addDurations(String... durations) {
        int totalSeconds = 0;

        for (String duration : durations) {
            if (duration != null && !duration.isEmpty()) {
                LocalTime time = LocalTime.parse(duration, TIME_FORMATTER);
                totalSeconds += time.toSecondOfDay();
            }
        }

        // Преобразуем общие секунды обратно в формат времени
        LocalTime totalTime = LocalTime.ofSecondOfDay(totalSeconds % (24 * 3600));
        return totalTime.format(TIME_FORMATTER);
    }

}

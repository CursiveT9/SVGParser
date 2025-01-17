package org.example.sutochnikweb.services;

import org.springframework.stereotype.Service;

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

    public int getHoursFromDuration(String duration) {
        if (duration != null && !duration.isEmpty()) {
            return Integer.parseInt(duration.substring(0, 2)); // Извлекаем первые две цифры
        }
        return 0; // Значение по умолчанию
    }

}

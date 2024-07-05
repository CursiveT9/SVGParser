// Перечисление для типов операций
public enum ActionType {
    TRAIN_ARRIVAL, // прием поезда, готово
    PASSENGER_TRAIN_ARRIVAL, // прием пассажирского поезда, готово
    PASSENGER_TRAIN_DEPARTURE, // отправление пассажирского поезда, готово
    TRAIN_DEPARTURE, // отправление поезда, готово
    SHUNTING, // перестановка, готово
    LOCOMOTIVE_CLEANING, // уборка поездного локомотива, готово
    LOCOMOTIVE_PROVISION, // подача поездного локомотива, готово
    ADVANCEMENT, // надвиг, готово
    LOCOMOTIVE_MOVEMENT_RESERVE, // движение локомотива резервом, готово
    SIDETRACK_CLEANING, // уборка с подъездного пути
    SIDETRACK_PROVISION // подача на подъездной путь
}
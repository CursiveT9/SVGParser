import java.util.Map;

class HeightRange {
    private final int start;
    private final String name;
    private int numPassengerTrainArrivalsAndDepartures;
    private Map<String, Integer> symbolCount;

    public HeightRange(int start, String name) {
        this.start = start;
        this.name = name;
    }

    public int getNumPassengerTrainArrivalsAndDepartures() {
        return numPassengerTrainArrivalsAndDepartures;
    }

    public void setNumPassengerTrainArrivalsAndDepartures(int numPassengerTrainArrivalsAndDepartures) {
        this.numPassengerTrainArrivalsAndDepartures = numPassengerTrainArrivalsAndDepartures;
    }

    public void incrementNumPassengerTrainArrivalsAndDepartures() {
        this.numPassengerTrainArrivalsAndDepartures ++;
    }

    public int getStart() {
        return start;
    }

    public String getName() {
        return name;
    }


}
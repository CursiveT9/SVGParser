class HeightRange {
    private final int start;
    private final String name;
    private int numPassengerTrainArrivalsAndDepartures;

    private int numLocomotiveArrival;

    private int numWaitingOfMovement;

    private int numDowntime;

    private int numWaitingForThread;

    private int numWaitingForBrigade;

    private int numDischarge;

    private int numTrainLocomotiveTrailer;

    private int numCouplingShuntingLocomotive;

    private double totalLengthLocomotiveArrival;
    private double totalLengthWaitingOfMovement;
    private double totalLengthDowntime;
    private double totalLengthWaitingForThread;
    private double totalLengthWaitingForBrigade;
    private double totalLengthPassengerTrainArrivalsAndDepartures;

    private double totalLengthTrainLocomotiveTrailer;

    private double totalLengthDischarge;
    private double totalLengthCouplingShuntingLocomotive;


    public void incrementNumPassengerTrainArrivalsAndDepartures() {
        this.numPassengerTrainArrivalsAndDepartures ++;
    }

    public void incrementLocomotiveArrival(){
        this.numLocomotiveArrival++;
    }
    public void incrementWaitingOfMovement(){
        this.numWaitingOfMovement++;
    }
    public void incrementDowntime(){
        this.numDowntime++;
    }
    public void incrementWaitingForThread(){
        this.numWaitingForThread++;
    }
    public void incrementWaitingForBrigade(){
        this.numWaitingForBrigade++;
    }
    public void incrementCargoOperations(){
        this.numDischarge++;
    }

    public void incrementTrainLocomotiveTrailer(){
        this.numTrainLocomotiveTrailer++;
    }

    public void incrementCouplingShuntingLocomotive(){
        this.numCouplingShuntingLocomotive++;
    }

    public void addTotalLengthPassengerTrainArrivalsAndDepartures(double width){
        this.totalLengthPassengerTrainArrivalsAndDepartures+=width;
    }
    public void addTotalLengthLocomotiveArrival(double rectWidthValue) {
        this.totalLengthLocomotiveArrival+=rectWidthValue;
    }

    public void addTotalLengthWaitingOfMovement(double rectWidthValue) {
        this.totalLengthWaitingOfMovement+=rectWidthValue;
    }

    public void addTotalLengthDowntime(double rectWidthValue){
        this.totalLengthDowntime+=rectWidthValue;
    }

    public void addTotalLengthWaitingForThread(double rectWidthValue) {
        this.totalLengthWaitingForThread+=rectWidthValue;
    }

    public void addTotalLengthWaitingForBrigade(double rectWidthValue) {
        this.totalLengthWaitingForBrigade+=rectWidthValue;
    }

    public void addTotalLengthCargoOperations(double width){
        this.totalLengthDischarge+=width;
    }

    public void addTotalLengthTrainLocomotiveTrailer(double width){
        this.totalLengthTrainLocomotiveTrailer+=width;
    }
    public void addTotalLengthCouplingShuntingLocomotive(double width){
        this.totalLengthCouplingShuntingLocomotive+=width;
    }

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

    public int getNumLocomotiveArrival() {
        return numLocomotiveArrival;
    }

    public void setNumLocomotiveArrival(int numLocomotiveArrival) {
        this.numLocomotiveArrival = numLocomotiveArrival;
    }
    public int getNumWaitingOfMovement() {
        return numWaitingOfMovement;
    }

    public void setNumWaitingOfMovement(int numWaitingOfMovement) {
        this.numWaitingOfMovement = numWaitingOfMovement;
    }

    public int getNumDowntime() {
        return numDowntime;
    }

    public void setNumDowntime(int numDowntime) {
        this.numDowntime = numDowntime;
    }

    public int getNumWaitingForThread() {
        return numWaitingForThread;
    }

    public void setNumWaitingForThread(int numWaitingForThread) {
        this.numWaitingForThread = numWaitingForThread;
    }

    public int getNumWaitingForBrigade() {
        return numWaitingForBrigade;
    }

    public void setNumWaitingForBrigade(int numWaitingForBrigade) {
        this.numWaitingForBrigade = numWaitingForBrigade;
    }

    public double getTotalLengthLocomotiveArrival() {
        return totalLengthLocomotiveArrival;
    }

    public void setTotalLengthLocomotiveArrival(double totalLengthLocomotiveArrival) {
        this.totalLengthLocomotiveArrival = totalLengthLocomotiveArrival;
    }

    public double getTotalLengthWaitingOfMovement() {
        return totalLengthWaitingOfMovement;
    }

    public void setTotalLengthWaitingOfMovement(double totalLengthWaitingOfMovement) {
        this.totalLengthWaitingOfMovement = totalLengthWaitingOfMovement;
    }

    public double getTotalLengthDowntime() {
        return totalLengthDowntime;
    }

    public void setTotalLengthDowntime(double totalLengthDowntime) {
        this.totalLengthDowntime = totalLengthDowntime;
    }

    public double getTotalLengthWaitingForThread() {
        return totalLengthWaitingForThread;
    }

    public void setTotalLengthWaitingForThread(double totalLengthWaitingForThread) {
        this.totalLengthWaitingForThread = totalLengthWaitingForThread;
    }

    public double getTotalLengthWaitingForBrigade() {
        return totalLengthWaitingForBrigade;
    }

    public void setTotalLengthWaitingForBrigade(double totalLengthWaitingForBrigade) {
        this.totalLengthWaitingForBrigade = totalLengthWaitingForBrigade;
    }
    public double getTotalLengthPassengerTrainArrivalsAndDepartures() {
        return totalLengthPassengerTrainArrivalsAndDepartures;
    }
    public void setTotalLengthPassengerTrainArrivalsAndDepartures(double totalLengthPassengerTrainArrivalsAndDepartures) {
        this.totalLengthPassengerTrainArrivalsAndDepartures = totalLengthPassengerTrainArrivalsAndDepartures;
    }

    public int getNumDischarge() {
        return numDischarge;
    }

    public void setNumDischarge(int numDischarge) {
        this.numDischarge = numDischarge;
    }

    public double getTotalLengthDischarge() {
        return totalLengthDischarge;
    }

    public void setTotalLengthCargoOperations(double totalLengthCargoOperations) {
        this.totalLengthDischarge = totalLengthCargoOperations;
    }

    public int getNumTrainLocomotiveTrailer() {
        return numTrainLocomotiveTrailer;
    }

    public void setNumTrainLocomotiveTrailer(int numTrainLocomotiveTrailer) {
        this.numTrainLocomotiveTrailer = numTrainLocomotiveTrailer;
    }

    public double getTotalLengthTrainLocomotiveTrailer() {
        return totalLengthTrainLocomotiveTrailer;
    }

    public void setTotalLengthTrainLocomotiveTrailer(double totalLengthTrainLocomotiveTrailer) {
        this.totalLengthTrainLocomotiveTrailer = totalLengthTrainLocomotiveTrailer;
    }

    public void setTotalLengthDischarge(double totalLengthDischarge) {
        this.totalLengthDischarge = totalLengthDischarge;
    }

    public int getNumCouplingShuntingLocomotive() {
        return numCouplingShuntingLocomotive;
    }

    public void setNumCouplingShuntingLocomotive(int numCouplingShuntingLocomotive) {
        this.numCouplingShuntingLocomotive = numCouplingShuntingLocomotive;
    }

    public double getTotalLengthCouplingShuntingLocomotive() {
        return totalLengthCouplingShuntingLocomotive;
    }

    public void setTotalLengthCouplingShuntingLocomotive(double totalLengthCouplingShuntingLocomotive) {
        this.totalLengthCouplingShuntingLocomotive = totalLengthCouplingShuntingLocomotive;
    }

    public int getStart() {
        return start;
    }

    public String getName() {
        return name;
    }



}
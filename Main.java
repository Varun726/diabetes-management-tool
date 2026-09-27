public class Main {
    public static void main(String[] args) {
        // File paths for patient profiles, food data, sugar level data, and foods consumed data
        String patientsFile = "data/patients.txt";
        String foodDataFile = "data/foods.csv";
        String sugarLevelDataFile = "data/sugar_levels.txt";
        String foodsConsumedDataFile = "data/foods_consumed.txt";

        // Create DiabetesManager object with file paths
        DiabetesManager diabetesManager = new DiabetesManager(patientsFile, foodDataFile, sugarLevelDataFile, foodsConsumedDataFile);

        // Call mainMenu method to start the program
        diabetesManager.mainMenu();
    }
}

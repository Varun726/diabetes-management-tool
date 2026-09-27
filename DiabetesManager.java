import java.io.*;
import java.util.*;
import java.time.*;
import java.util.stream.*;

public class DiabetesManager {
    private List<Patient> patients;
    private FoodDatabase foodDatabase;
    private String patientsFile;
    private String foodDataFile;
    private String sugarLevelDataFile;
    private String foodsConsumedDataFile;

    public DiabetesManager(String patientsFile, String foodDataFile, String sugarLevelDataFile, String foodsConsumedDataFile) {
        this.patients = new ArrayList<>();
        this.patientsFile = patientsFile;
        this.foodDataFile = foodDataFile;
        this.sugarLevelDataFile = sugarLevelDataFile;
        this.foodsConsumedDataFile = foodsConsumedDataFile;
        this.foodDatabase = new FoodDatabase(foodDataFile);
        loadPatientsFromFile();
    }

    private void loadPatientsFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(patientsFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                patients.add(new Patient(line.trim(), sugarLevelDataFile, foodsConsumedDataFile));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void savePatientToFile(String name) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(patientsFile, true))) {
            bw.newLine();
            bw.write(name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Patient createPatient(String name) {
        Patient patient = new Patient(name, sugarLevelDataFile, foodsConsumedDataFile);
        patients.add(patient);
        savePatientToFile(name);
        return patient;
    }

    public void enterSugarLevelAndFoods(Patient patient, String date, List<Integer> sugarLevels, List<String> foods) {
        patient.addSugarLevels(date, sugarLevels);
        patient.addFoodsConsumed(date, foods);
    }

    public void viewSummaryAnalytics() {
        System.out.println("Summary Analytics:");

        for (Patient patient : patients) {
            System.out.println("Patient: " + patient.getName());

            // Display sugar levels
            Map<String, List<Integer>> sugarLevels = patient.getSugarLevels();
            if (!sugarLevels.isEmpty()) {
                System.out.println("Sugar Levels:");
                for (Map.Entry<String, List<Integer>> entry : sugarLevels.entrySet()) {
                    String date = entry.getKey();
                    List<Integer> levels = entry.getValue();
                    System.out.println("Date: " + date + ", Levels: " + levels);
                }
            }

	    // Display foods consumed
            Map<String, List<String>> foodsConsumed = patient.getFoodsConsumed();
            if (!foodsConsumed.isEmpty()) {
                System.out.println("Foods Consumed:");
                for (Map.Entry<String, List<String>> entry : foodsConsumed.entrySet()) {
                    String date = entry.getKey();
                    List<String> foods = entry.getValue();
                    System.out.println("Date: " + date + ", Foods: " + String.join(", ", foods));
                }
            }

            System.out.println(); // Add an empty line for readability
        }
    }

    public void mainMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("Main Menu");
            System.out.println("1. Create Patient");
            System.out.println("2. Enter Sugar Level and Foods");
            System.out.println("3. View Summary Analytics");
            System.out.println("4. Show Top 5 Foods Contributing to Highest Sugar Level");
            System.out.println("5. Show Top 5 Foods Contributing to Lowest Sugar Level");
	    System.out.println("6. Show Food Database");
            System.out.println("7. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline character

            switch (choice) {
                case 1:
                    System.out.print("Enter patient's name: ");
                    String patientName = scanner.nextLine();
                    createPatient(patientName);
                    break;
                case 2:
                    System.out.print("Enter patient's name: ");
                    String patientName2 = scanner.nextLine();
                    System.out.print("Enter date (YYYY-MM-DD): ");
                    String date = scanner.nextLine();
                    System.out.print("Enter sugar levels (comma-separated): ");
                    String sugarLevelsStr = scanner.nextLine();
                    List<Integer> sugarLevels = Arrays.stream(sugarLevelsStr.split(","))
                            .map(Integer::parseInt)
                            .collect(Collectors.toList());
                    System.out.print("Enter foods consumed (comma-separated): ");
                    String foodsConsumed = scanner.nextLine();
                    enterSugarLevelAndFoods(findPatient(patientName2), date, sugarLevels, Arrays.asList(foodsConsumed.split(",")));
                    break;
                case 3:
                    System.out.println("Viewing Summary Analytics...");
                    viewSummaryAnalytics();
                    break;
                case 4:
                    showTopFoodsContributingToHighestSugarLevel();
                    break;
                case 5:
                    showTopFoodsContributingToLowestSugarLevel();
                    break;
                case 6:
                    showFoodDatabase();
                    break;
                case 7:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid option! Please choose again.");
            }
        }

        scanner.close();
    }

    private void showFoodDatabase() {
        System.out.println("Food Database:");
        System.out.println("Food Name\tGlycemic Index");
        foodDatabase.getFoods().forEach((food, glycemicIndex) ->
                System.out.println(food + "\t\t" + glycemicIndex));
    }

    private void showTopFoodsContributingToHighestSugarLevel() {
        Map<String, Integer> foodSugarLevels = new HashMap<>();
        for (Patient patient : patients) {
            Map<String, List<String>> foodsConsumed = patient.getFoodsConsumed();
            for (List<String> foods : foodsConsumed.values()) {
                for (String food : foods) {
                    foodSugarLevels.put(food, foodSugarLevels.getOrDefault(food, 0) + 1);
                }
            }
        }

        if (foodSugarLevels.isEmpty()) {
            System.out.println("No foods consumed data available.");
            return;
        }

        System.out.println("Top 5 Foods Contributing to Highest Sugar Level:");
        foodSugarLevels.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue() + " times"));
    }

    private void showTopFoodsContributingToLowestSugarLevel() {
        Map<String, Integer> foodSugarLevels = new HashMap<>();
        for (Patient patient : patients) {
            Map<String, List<String>> foodsConsumed = patient.getFoodsConsumed();
            for (List<String> foods : foodsConsumed.values()) {
                for (String food : foods) {
                    foodSugarLevels.put(food, foodSugarLevels.getOrDefault(food, 0) + 1);
                }
            }
        }

        if (foodSugarLevels.isEmpty()) {
            System.out.println("No foods consumed data available.");
            return;
        }

        System.out.println("Top 5 Foods Contributing to Lowest Sugar Level:");
        foodSugarLevels.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue())
                .limit(5)
                .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue() + " times"));
    }
    private Patient findPatient(String name) {
        for (Patient patient : patients) {
            if (patient.getName().equals(name)) {
                return patient;
            }
        }
        System.out.println("Patient not found.");
        return null;
    }
}

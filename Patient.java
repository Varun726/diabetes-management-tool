import java.io.*;
import java.util.*;

public class Patient {
    private String name;
    private Map<String, List<Integer>> sugarLevels;
    private Map<String, List<String>> foodsConsumed;
    private String sugarLevelDataFile;
    private String foodsConsumedDataFile;

    public Patient(String name, String sugarLevelDataFile, String foodsConsumedDataFile) {
        this.name = name;
        this.sugarLevelDataFile = sugarLevelDataFile;
        this.foodsConsumedDataFile = foodsConsumedDataFile;
        this.sugarLevels = new HashMap<>();
        this.foodsConsumed = new HashMap<>();
        loadSugarLevelsFromFile();
        loadFoodsConsumedFromFile();
    }

    private void loadSugarLevelsFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(sugarLevelDataFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 3);
                if (parts.length == 3 && parts[0].equals(name)) {
                    String date = parts[1];
                    String[] sugarLevelsStr = parts[2].split(",");
                    List<Integer> levels = new ArrayList<>();
                    for (String levelStr : sugarLevelsStr) {
                        levels.add(Integer.parseInt(levelStr.trim()));
                    }
                    sugarLevels.put(date, levels);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFoodsConsumedFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(foodsConsumedDataFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 3);
                if (parts.length == 3 && parts[0].equals(name)) {
                    String date = parts[1];
                    String[] foods = parts[2].split(",");
                    foodsConsumed.put(date, Arrays.asList(foods));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public Map<String, List<Integer>> getSugarLevels() {
        return sugarLevels;
    }

    public Map<String, List<String>> getFoodsConsumed() {
        return foodsConsumed;
    }

    public void addSugarLevels(String date, List<Integer> levels) {
        sugarLevels.put(date, levels);
        saveSugarLevelsToFile();
    }

    private void saveSugarLevelsToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(sugarLevelDataFile, true))) {
            for (Map.Entry<String, List<Integer>> entry : sugarLevels.entrySet()) {
                String date = entry.getKey();
                List<Integer> levels = entry.getValue();
                bw.newLine();
                bw.write(name + "," + date + "," + levels.stream().map(Object::toString).reduce((s1, s2) -> s1 + "," + s2).orElse(""));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addFoodsConsumed(String date, List<String> foods) {
        foodsConsumed.put(date, foods);
        saveFoodsConsumedToFile();
    }

    private void saveFoodsConsumedToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(foodsConsumedDataFile, true))) {
            for (Map.Entry<String, List<String>> entry : foodsConsumed.entrySet()) {
                String date = entry.getKey();
                List<String> foods = entry.getValue();
                bw.newLine();
                bw.write(name + "," + date + "," + String.join(",", foods));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

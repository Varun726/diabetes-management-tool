import java.io.*;
import java.util.*;

public class FoodDatabase {
    private String foodDataFile;
    private Map<String, Integer> foods;

    public FoodDatabase(String foodDataFile) {
        this.foodDataFile = foodDataFile;
        this.foods = new HashMap<>();
        loadFoodsFromFile();
    }

    private void loadFoodsFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(foodDataFile))) {
            String line;
            boolean headerSkipped = false;
            while ((line = br.readLine()) != null) {
                if (!headerSkipped) {
                    // Skip header line
                    headerSkipped = true;
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String foodName = parts[0].trim();
                    int glycemicIndex = Integer.parseInt(parts[1].trim());
                    foods.put(foodName, glycemicIndex);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Integer getGlycemicIndex(String foodName) {
        return foods.getOrDefault(foodName, 0); // Default value if food not found
    }
    
    public Map<String, Integer> getFoods() {
        return foods;
    }
}

package engine;

import java.util.*;
import java.util.stream.Collectors;

public class SelectEngine {

    public static List<Map<String, Object>> projectColumns(List<String> columns, List<Map<String, Object>> data) {
        // Select only specific columns from data
        return data.stream()
                   .map(row -> {
                       Map<String, Object> projectedRow = row.entrySet().stream()
                           .filter(entry -> columns.contains(entry.getKey()))
                           .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                       return projectedRow;
                   })
                   .collect(Collectors.toList());
    }
    public static void main(String[] args) {
        List<Map<String, Object>> data = new ArrayList<>();
        Map<String, Object> row1 = new HashMap<>();
        row1.put("id", 1);
        row1.put("name", "Alice");
        row1.put("age", 25);
        data.add(row1);

        Map<String, Object> row2 = new HashMap<>();
        row2.put("id", 2);
        row2.put("name", "Bob");
        row2.put("age", 30);
        data.add(row2);

        // Columns to project
        List<String> columns = Arrays.asList("id", "name");

        // Expected result
        List<Map<String, Object>> expected = new ArrayList<>();
        Map<String, Object> expectedRow1 = new HashMap<>();
        expectedRow1.put("id", 1);
        expectedRow1.put("name", "Alice");
        expected.add(expectedRow1);

        Map<String, Object> expectedRow2 = new HashMap<>();
        expectedRow2.put("id", 2);
        expectedRow2.put("name", "Bob");
        expected.add(expectedRow2);

        // Call the method
        List<Map<String, Object>> result = projectColumns(columns, data);

        // Print the result in tabular format
        System.out.println("Projected Data:");
        System.out.println("----------------");
        for (Map<String, Object> row : result) {
            row.forEach((key, value) -> System.out.print(key + ": " + value + "\t"));
            System.out.println();
        }
    }
}
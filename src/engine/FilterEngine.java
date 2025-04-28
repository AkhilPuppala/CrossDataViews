package engine;

import model.Filter;
import java.util.*;   // Correct import!

public class FilterEngine {

    public static List<Map<String, Object>> applyFilters(List<Filter> filters, List<Map<String, Object>> data) {
        if (filters == null || filters.isEmpty()) {
            return data; // No filters, return original
        }

        List<Map<String, Object>> filteredData = new ArrayList<>();

        for (Map<String, Object> row : data) {
            boolean match = true;

            for (Filter filter : filters) {
                if (!evaluateCondition(filter.condition, row)) {
                    match = false;
                    break;
                }
            }

            if (match) {
                filteredData.add(row);
            }
        }

        return filteredData;
    }

    private static boolean evaluateCondition(String condition, Map<String, Object> row) {
        // Only handle simple conditions like "i.Total = 200.0" or "c.ContactName = 'John Doe'"
        String[] parts = condition.split("=");

        if (parts.length != 2) {
            throw new IllegalArgumentException("Only simple '=' conditions supported: " + condition);
        }

        String field = parts[0].trim();     // e.g., "i.Total"
        String valueRaw = parts[1].trim();   // e.g., "200.0" or "'John Doe'"

        Object actualValue = row.get(field);

        // Remove quotes if value is a string literal
        if (valueRaw.startsWith("'") && valueRaw.endsWith("'")) {
            valueRaw = valueRaw.substring(1, valueRaw.length() - 1);
        }

        if (actualValue == null) {
            return false;
        }

        // Compare based on type
        if (actualValue instanceof Number) {
            try {
                double expected = Double.parseDouble(valueRaw);
                return Double.compare(((Number) actualValue).doubleValue(), expected) == 0;
            } catch (NumberFormatException e) {
                return false;
            }
        } else {
            return actualValue.toString().equals(valueRaw);
        }
    }

    public static void main(String[] args) {
        // Sample data after join
        List<Map<String, Object>> joinedData = new ArrayList<>();

        Map<String, Object> row1 = new HashMap<>();
        row1.put("i.InvoiceId", 1);
        row1.put("i.Total", 200.0);
        row1.put("c.ContactName", "John Doe");
        joinedData.add(row1);

        Map<String, Object> row2 = new HashMap<>();
        row2.put("i.InvoiceId", 2);
        row2.put("i.Total", 400.0);
        row2.put("c.ContactName", "Jane Smith");
        joinedData.add(row2);

        Map<String, Object> row3 = new HashMap<>();
        row3.put("i.InvoiceId", 3);
        row3.put("i.Total", 200.0);
        row3.put("c.ContactName", "Jane Smith");
        joinedData.add(row3);

        // Define filters
        List<Filter> filters = Arrays.asList(
            new Filter("i.Total = 200.0"),
            new Filter("c.ContactName = 'John Doe'")
        );

        // Apply filters (no need to write engine.FilterEngine here)
        List<Map<String, Object>> filtered = applyFilters(filters, joinedData);

        // Print filtered results
        System.out.println("Filtered Result:");
        for (Map<String, Object> record : filtered) {
            System.out.println(record);
        }
    }
}

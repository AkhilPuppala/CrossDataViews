package engine;

import model.Join;
import java.util.*;

public class JoinEngine {
    
    public static List<Map<String, Object>> performJoins(List<Join> joins, Map<String, List<Map<String, Object>>> tableData) {
        if (joins == null || joins.isEmpty()) {
            // No joins to perform
            return new ArrayList<>();
        }

        List<Map<String, Object>> result = new ArrayList<>(tableData.get(joins.get(0).leftAlias));

        for (Join join : joins) {
            List<Map<String, Object>> rightTable = tableData.get(join.rightAlias);
            result = joinTwoTables(result, rightTable, join);
        }

        return result;
    }

    private static List<Map<String, Object>> joinTwoTables(List<Map<String, Object>> left, List<Map<String, Object>> right, Join join) {
        List<Map<String, Object>> joined = new ArrayList<>();

        // Parse ON condition like "i.InvoiceId = il.InvoiceId"
        String[] parts = join.onCondition.split("=");
        String leftField = parts[0].trim();
        String rightField = parts[1].trim();

        for (Map<String, Object> leftRow : left) {
            for (Map<String, Object> rightRow : right) {
                Object leftValue = leftRow.get(leftField);
                Object rightValue = rightRow.get(rightField);

                if (Objects.equals(leftValue, rightValue)) {
                    Map<String, Object> combined = new HashMap<>();
                    combined.putAll(leftRow);
                    combined.putAll(rightRow);
                    joined.add(combined);
                }
            }
        }

        return joined;
    }

    public static void main(String[] args) {
        // Table a
        List<Map<String, Object>> tableA = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Map<String, Object> row = new HashMap<>();
            row.put("AId", i);
            row.put("Name", "Name" + i);
            row.put("Age", 20 + i);
            tableA.add(row);
        }
    
        // Table b
        List<Map<String, Object>> tableB = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Map<String, Object> row = new HashMap<>();
            row.put("AId", i);  // matches AId from tableA
            row.put("BId", i + 100);
            row.put("Score", 50 + i);
            tableB.add(row);
        }
    
        // Table c
        List<Map<String, Object>> tableC = new ArrayList<>();
        for (int i = 101; i <= 110; i++) {
            Map<String, Object> row = new HashMap<>();
            row.put("BId", i); // matches BId from tableB
            row.put("City", "City" + (i - 100));
            row.put("Zip", "1000" + (i - 100));
            tableC.add(row);
        }
    
        // Put into tableData map with aliases
        Map<String, List<Map<String, Object>>> tableData = new HashMap<>();
        tableData.put("a", tableA);
        tableData.put("b", tableB);
        tableData.put("c", tableC);
    
        // Define joins
        List<Join> joins = new ArrayList<>();
        joins.add(new Join("INNER", "a", "b", "AId = AId"));   // a.AId = b.AId
        joins.add(new Join("INNER", "b", "c", "BId = BId"));   // b.BId = c.BId
    
        // Perform joins
        List<Map<String, Object>> result = performJoins(joins, tableData);
    
        // Print results
        System.out.println("Joined Result:");
        for (Map<String, Object> row : result) {
            System.out.println(row);
        }
    }
    
}

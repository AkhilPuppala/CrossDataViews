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

    // -----------------------
    // 🧪 Hardcoded test here:
    public static void main(String[] args) {
        // 1. Hardcoded table data
        List<Map<String, Object>> invoiceTable = new ArrayList<>();
        List<Map<String, Object>> invoiceLineTable = new ArrayList<>();
        List<Map<String, Object>> customerTable = new ArrayList<>();
    
        // Invoice table (alias i)
        Map<String, Object> invoice1 = new HashMap<>();
        invoice1.put("i.InvoiceId", 1);
        invoice1.put("i.CustomerId", 101);
        invoice1.put("i.Total", 200.0);
        invoiceTable.add(invoice1);
    
        Map<String, Object> invoice2 = new HashMap<>();
        invoice2.put("i.InvoiceId", 2);
        invoice2.put("i.CustomerId", 102);
        invoice2.put("i.Total", 400.0);
        invoiceTable.add(invoice2);
    
        // InvoiceLine table (alias il)
        Map<String, Object> line1 = new HashMap<>();
        line1.put("il.InvoiceId", 1);
        line1.put("il.InvoiceLineId", 1001);
        line1.put("il.Quantity", 5);
        invoiceLineTable.add(line1);
    
        Map<String, Object> line2 = new HashMap<>();
        line2.put("il.InvoiceId", 1);
        line2.put("il.InvoiceLineId", 1002);
        line2.put("il.Quantity", 3);
        invoiceLineTable.add(line2);
    
        Map<String, Object> line3 = new HashMap<>();
        line3.put("il.InvoiceId", 3);
        line3.put("il.InvoiceLineId", 1003);
        line3.put("il.Quantity", 2);
        invoiceLineTable.add(line3);
    
        // Customer table (alias c)
        Map<String, Object> cust1 = new HashMap<>();
        cust1.put("c.CustomerId", 101);
        cust1.put("c.ContactName", "John Doe");
        customerTable.add(cust1);
    
        Map<String, Object> cust2 = new HashMap<>();
        cust2.put("c.CustomerId", 102);
        cust2.put("c.ContactName", "Jane Smith");
        customerTable.add(cust2);
    
        // 2. Prepare tableData map
        Map<String, List<Map<String, Object>>> tableData = new HashMap<>();
        tableData.put("i", invoiceTable);
        tableData.put("il", invoiceLineTable);
        tableData.put("c", customerTable);
    
        // 3. Create 2 Join objects
        List<Join> joins = new ArrayList<>();
        joins.add(new Join("INNER", "i", "il", "InvoiceId = InvoiceId"));    // Join Invoice and InvoiceLine
        joins.add(new Join("INNER", "i", "c", "CustomerId = CustomerId"));    // Join Invoice and Customer
    
        // 4. Perform join
        List<Map<String, Object>> joinedResult = performJoins(joins, tableData);
    
        // 5. Print output
        System.out.println("Joined Result:");
        for (Map<String, Object> row : joinedResult) {
            System.out.println(row);
        }
    }    
}

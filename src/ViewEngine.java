import model.*;
// import parser.*;
import db.*;
import engine.*;
// import java.io.FileWriter;
// import java.io.IOException;
// import com.google.gson.Gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileWriter;
import java.io.IOException;


import java.util.*;

public class ViewEngine {
    public static void main(String[] args) {
        
        View view = new View("data/output.xml");
        
        Catalog catalog = new Catalog("catalog.xml");

        System.out.println(catalog.databases.size());

        Map<String, List<Map<String, Object>>> tableData = new HashMap<>();

        for (Table table : view.tables) {
            System.out.println(table.name);
            String dbName = catalog.getDbNameForTable(table.name);
            System.out.println(dbName);
            DatabaseInfo dbInfo = catalog.databases.get(dbName);
            

            Fetcher fetcher = FetcherFactory.getFetcher(dbInfo.connection.type);
            List<Map<String, Object>> data = fetcher.fetchData(table.name, dbInfo.connection);

            tableData.put(table.alias, data);
        }


        
        
        // String url = mysqlUrl;
        // String user = mysqlUser;
        // String password = mysqlPassword;

        // MySQLFetcher f1 = new MySQLFetcher();
        // ConnectionInfo connection1 = new ConnectionInfo("mysql", url, user, "passcode", "InvoiceLine", "3306");
        // List<Map<String, Object>> d1 = f1.fetchData("InvoiceLine", connection1);

        // RecursiveBaseXFetcher f2 = new RecursiveBaseXFetcher();
        // ConnectionInfo connection2 = new ConnectionInfo("xml","localhost","akhil","passcode","Invoice","1984");
        // List<Map<String, Object>> d2 = f2.fetchData("Invoice", connection2);

        // // List<Join> joins = new ArrayList<>();
        // // joins.add(new Join("INNER", "i", "il", "i.InvoiceId = il.InvoiceId"));
        
        // Map<String, List<Map<String, Object>>> tableData = new HashMap<>();
        // tableData.put("i", d2);
        // tableData.put("il", d1);
        
        List<Map<String, Object>> joined = JoinEngine.performJoins(view.joins, tableData);
        List<Map<String, Object>> filtered = FilterEngine.applyFilters(view.filters, joined);
        List<Map<String, Object>> finalResult = SelectEngine.projectColumns(view.selectColumns, filtered);

        System.out.println(finalResult.size());

        // Write finalResult to a JSON file
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter("data/output.json")) {
            gson.toJson(finalResult, writer);
            System.out.println("Final result has been written to data/output.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

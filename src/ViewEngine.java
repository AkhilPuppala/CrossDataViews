import model.*;
import parser.*;
import db.*;
import engine.*;
import java.io.FileWriter;
import java.io.IOException;
import com.google.gson.Gson;


import java.util.*;

public class ViewEngine {
    public static void main(String[] args) {
        
        View view = new View("data/output.xml");
        
        // Catalog catalog = Catalog("data/Catalog.xml");

        // Map<String, List<Map<String, Object>>> tableData = new HashMap<>();

        // for (Table table : view.tables) {
        //     String dbName = catalog.getDatabaseForTable(table.name);
        //     DatabaseConnectionInfo dbInfo = catalog.databases.get(dbName);
        //     TableSchema schema = dbInfo.tables.get(table.name);

        //     Fetcher fetcher = FetcherFactory.getFetcher(dbInfo.connection.type);
        //     List<Map<String, Object>> data = fetcher.fetchData(table.name, dbInfo.connection, schema);

        //     tableData.put(table.alias, data);
        // }

        String url = "jdbc:mysql://localhost:3306/InvoiceLine";
        String user = "root";
        String password = "passcode";

        MySQLFetcher f1 = new MySQLFetcher();
        ConnectionInfo connection1 = new ConnectionInfo("mysql", url, user, "passcode", "InvoiceLine", "3306");
        List<Map<String, Object>> d1 = f1.fetchData("InvoiceLine", connection1);

        RecursiveBaseXFetcher f2 = new RecursiveBaseXFetcher();
        ConnectionInfo connection2 = new ConnectionInfo("xml","localhost","akhil","passcode","Invoice","1984");
        List<Map<String, Object>> d2 = f2.fetchData("Invoice", connection2);

        // List<Join> joins = new ArrayList<>();
        // joins.add(new Join("INNER", "i", "il", "i.InvoiceId = il.InvoiceId"));
        
        Map<String, List<Map<String, Object>>> tableData = new HashMap<>();
        tableData.put("i", d2);
        tableData.put("il", d1);
        
        List<Map<String, Object>> joined = JoinEngine.performJoins(view.joins, tableData);
        List<Map<String, Object>> filtered = FilterEngine.applyFilters(view.filters, joined);
        List<Map<String, Object>> finalResult = SelectEngine.projectColumns(view.selectColumns, filtered);

        System.out.println(finalResult.size());
    }
}

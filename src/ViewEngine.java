import model.*;
import db.*;
import engine.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileWriter;
import java.util.*;

public class ViewEngine {

    /**
     * Processes a view file and catalog file, performs operations, and writes the final result to an output file.
     *
     * @param viewFilePath   The path to the view XML file.
     * @param catalogFilePath The path to the catalog XML file.
     * @param outputFilePath The path to the output JSON file.
     */
    public static void viewToOutput(String viewFilePath, String catalogFilePath, String outputFilePath) {
        try {
            // Load the view and catalog
            View view = new View(viewFilePath);
            Catalog catalog = new Catalog(catalogFilePath);

            System.out.println("Number of databases in catalog: " + catalog.databases.size());

            // Map to store table data
            Map<String, List<Map<String, Object>>> tableData = new HashMap<>();

            // Fetch data for each table in the view
            for (Table table : view.tables) {
                System.out.println("Processing table: " + table.name);
                String dbName = catalog.getDbNameForTable(table.name);
                System.out.println("Database for table: " + dbName);
                DatabaseInfo dbInfo = catalog.databases.get(dbName);

                Fetcher fetcher = FetcherFactory.getFetcher(dbInfo.connection.type);
                List<Map<String, Object>> data = fetcher.fetchData(table.name, dbInfo.connection);

                tableData.put(table.alias, data);
            }

            // Perform joins, filters, and projections
            List<Map<String, Object>> joined = JoinEngine.performJoins(view.joins, tableData);
            List<Map<String, Object>> filtered = FilterEngine.applyFilters(view.filters, joined);
            List<Map<String, Object>> finalResult = SelectEngine.projectColumns(view.selectColumns, filtered);

            System.out.println("Number of records in final result: " + finalResult.size());

            // Write the final result to the output file
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try (FileWriter writer = new FileWriter(outputFilePath)) {
                gson.toJson(finalResult, writer);
                System.out.println("Final result has been written to " + outputFilePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String viewFilePath = "viewDefinitions/view1.xml";
        String catalogFilePath = "config/catalog.xml";
        String outputFilePath = "outputs/output1.xml";
        
        // Call the viewToOutput function with the provided arguments
        viewToOutput(viewFilePath,catalogFilePath,outputFilePath);
    }
}
import model.*;
import parser.*;
import db.*;
import engine.*;

import java.util.*;

public class ViewEngine {
    public static void main(String[] args) {
        View view = ViewParser.parse("data/View.xml");
        Catalog catalog = CatalogParser.parse("data/Catalog.xml");

        Map<String, List<Map<String, Object>>> tableData = new HashMap<>();

        for (Table table : view.tables) {
            String dbName = catalog.getDatabaseForTable(table.name);
            DatabaseConnectionInfo dbInfo = catalog.databases.get(dbName);
            TableSchema schema = dbInfo.tables.get(table.name);

            Fetcher fetcher = FetcherFactory.getFetcher(dbInfo.connection.type);
            List<Map<String, Object>> data = fetcher.fetchData(table.name, dbInfo.connection, schema);

            tableData.put(table.alias, data);
        }

        List<Map<String, Object>> joined = JoinEngine.performJoins(view.joins, tableData);
        List<Map<String, Object>> filtered = FilterEngine.applyFilters(view.filters, joined);
        List<Map<String, Object>> finalResult = SelectEngine.projectColumns(view.selectColumns, filtered);

        for (Map<String, Object> record : finalResult) {
            System.out.println(record);
        }
    }
}

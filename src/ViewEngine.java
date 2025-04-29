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

    static String mysqlUrl, mysqlUser, mysqlPassword, mysqlDbName, mysqlTableName;
    static String basexHost, basexUser, basexPassword, basexDbName, basexTableName;
    static int basexPort;

    public static void loadConfig(String filePath) throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder        dBuilder = dbFactory.newDocumentBuilder();
        Document               doc      = dBuilder.parse(new File(filePath));
        doc.getDocumentElement().normalize();
    
        Element root = doc.getDocumentElement();
        NodeList dbList = root.getElementsByTagName("DataBase");
    
        for (int i = 0; i < dbList.getLength(); i++) {
            Element dbElem = (Element) dbList.item(i);
            String  name   = dbElem.getElementsByTagName("name")
                                .item(0)
                                .getTextContent()
                                .trim();
    
            Element conn = (Element) dbElem.getElementsByTagName("Connection").item(0);
            String type  = conn.getElementsByTagName("type").item(0).getTextContent().trim();
            String table = dbElem.getElementsByTagName("Table").item(0).getTextContent().trim();
    
            if ("mysql".equalsIgnoreCase(type)) {
                mysqlDbName    = name;
                mysqlUrl       = conn.getElementsByTagName("host").item(0).getTextContent().trim();
                mysqlUser      = conn.getElementsByTagName("username").item(0).getTextContent().trim();
                mysqlPassword  = conn.getElementsByTagName("password").item(0).getTextContent().trim();
                mysqlTableName = table;
            }
            else if ("basex".equalsIgnoreCase(type)) {
                basexDbName    = name;
                basexHost      = conn.getElementsByTagName("host").item(0).getTextContent().trim();
                basexPort      = Integer.parseInt(conn.getElementsByTagName("port").item(0).getTextContent().trim());
                basexUser      = conn.getElementsByTagName("username").item(0).getTextContent().trim();
                basexPassword  = conn.getElementsByTagName("password").item(0).getTextContent().trim();
                basexTableName = table;
            }
        }
    }


    public static void main(String[] args) {
        
        View view = new View("data/output.xml");
        
        // Catalog catalog = Catalog("data/Catalog.xml");

        // Map<String, List<Map<String, Object>>> tableData = new HashMap<>();

        // for (Table table : view.tables) {
        //     String dbName = catalog.getDatabaseForTable(table.name);
        //     DatabaseConnectionInfo dbInfo = catalog.databases.get(dbName);
        //     TableSchema schema = dbInfo.tables.get(table.name);

        //     Fetcher fetcher = FetcherFactory.getFetcher(dbInfo.connection.type);
        //     List<Map<String, Object>> data = fetcher.fetchData(table.name, dbInfo.connection);

        //     tableData.put(table.alias, data);
        // }
        
        try {
            loadConfig("catalog.xml");
        } catch (Exception e) {
            // TODO: handle exception
        }

        String url = mysqlUrl;
        String user = mysqlUser;
        String password = mysqlPassword;

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

package db;

import model.ConnectionInfo;
// import model.TableSchema;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MySQLFetcher implements Fetcher {
    @Override
    public List<Map<String, Object>>  fetchData(String tableName, ConnectionInfo connectionInfo) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        String query = "SELECT * FROM " + tableName;

        try (Connection connection = DriverManager.getConnection(
            connectionInfo.getHost(), connectionInfo.getUser(), connectionInfo.getPassword());
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), resultSet.getObject(i));
                }
                resultList.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
        }

        return resultList;
    }

    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/InvoiceLine";
        String user = "root";
        String password = "passcode";
        
        ConnectionInfo connection = new ConnectionInfo("mysql", url, user, "passcode", "InvoiceLine", "3306");
        
        MySQLFetcher fetcher = new MySQLFetcher();
        List<Map<String, Object>> data = fetcher.fetchData("InvoiceLine", connection);

        // Print the fetched data
        System.out.println("Fetched Data:");
        System.out.println("Number of records: " + data.size());

        if (!data.isEmpty()) {
            System.out.println("First Record: " + data.get(0));
        } else {
            System.out.println("No records found.");
        }
        
    }
}
package db;

import model.ConnectionInfo;
import model.TableSchema;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MySQLFetcher implements Fetcher {
    @Override
    public List<Map<String, Object>> fetchData(String tableName, ConnectionInfo connectionInfo, TableSchema schema) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        String query = "SELECT * FROM " + tableName;

        try (Connection connection = DriverManager.getConnection(
                connectionInfo.getUrl(), connectionInfo.getUsername(), connectionInfo.getPassword());
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
            e.printStackTrace(); // Consider using a logger for better error handling
        }

        return resultList;
    }
}
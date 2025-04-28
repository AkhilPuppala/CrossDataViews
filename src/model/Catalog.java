package model;

import java.util.Map;

public class Catalog {
    public Map<String, DatabaseConnectionInfo> databases;

    public Catalog(Map<String, DatabaseConnectionInfo> databases) {
        this.databases = databases;
    }

    public String getDatabaseForTable(String tableName) {
        for (Map.Entry<String, DatabaseConnectionInfo> entry : databases.entrySet()) {
            if (entry.getValue().tables.containsKey(tableName)) {
                return entry.getKey();
            }
        }
        return null;
    }
}

package model;

import java.util.Map;

public class DatabaseConnectionInfo {
    public ConnectionInfo connection;
    public Map<String, TableSchema> tables;

    public DatabaseConnectionInfo(ConnectionInfo connection, Map<String, TableSchema> tables) {
        this.connection = connection;
        this.tables = tables;
    }
}

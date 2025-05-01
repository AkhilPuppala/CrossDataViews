package model;

import java.util.List;
import java.util.Map;
import java.util.HashSet;

public class DatabaseInfo {
    public ConnectionInfo connection;
    public HashSet<String> tables;

public DatabaseInfo(ConnectionInfo connection, HashSet<String> tables) {
    this.connection = connection;
    this.tables = tables;
}

    
}
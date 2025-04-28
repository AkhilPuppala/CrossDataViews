package db;

import model.ConnectionInfo;
import model.TableSchema;
import java.util.List;
import java.util.Map;

public class MySQLFetcher implements Fetcher {
    @Override
    public List<Map<String, Object>> fetchData(String tableName, ConnectionInfo connectionInfo, TableSchema schema) {
        // Your MySQL data fetching logic
        return null; // replace with real implementation
    }
}

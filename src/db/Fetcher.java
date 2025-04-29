package db;

import model.ConnectionInfo;
import model.TableSchema;
import java.util.List;
import java.util.Map;

public interface Fetcher {
    List<Map<String, Object>> fetchData(String InvoiceLine, ConnectionInfo connectionInfo);
}

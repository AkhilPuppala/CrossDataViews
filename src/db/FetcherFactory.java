package db;

public class FetcherFactory {

    private static final MySQLFetcher mysqlFetcher = new MySQLFetcher();
    private static final BaseXFetcher baseXFetcher = new BaseXFetcher();

    public static Fetcher getFetcher(String dbType) {
        if (dbType.equalsIgnoreCase("mysql")) {
            return mysqlFetcher;
        } else if (dbType.equalsIgnoreCase("basex")) {
            return baseXFetcher;
        } else {
            throw new UnsupportedOperationException("Unsupported database type: " + dbType);
        }
    }
}

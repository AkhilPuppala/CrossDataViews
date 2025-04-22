import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import org.basex.api.client.ClientSession;


// TO DO LIST 
// 1) Take login credentials in form of file 
// PROCESSOR - tells us which queries to execture based on view 
// 2) Implementing the queries 
// 3) Display Output 

query

public class Main {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/neil"; 
        String user = "root"; 
        String password = "newpassword"; 

           String basexHost = "localhost";  
           int basexPort = 1984;            
           String basexUser = "neil";      
           String basexPass = "newpassword";      



        try {
           
            Class.forName("com.mysql.cj.jdbc.Driver");

         
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Connected to MySQL successfully!");


            Statement stmt = conn.createStatement();

            String createTableSQL = "CREATE TABLE IF NOT EXISTS users ("
            + "id INT AUTO_INCREMENT PRIMARY KEY, "
            + "name VARCHAR(100), "
            + "email VARCHAR(100))";

            stmt.executeUpdate(createTableSQL);
            System.out.println("✅ Table 'users' created successfully!");
            
            String insertDataSQL = "INSERT INTO users (name, email) VALUES "
            + "('John Doe', 'john@example.com'),"
            + "('Alice Smith', 'alice@example.com')";

            stmt.executeUpdate(insertDataSQL);
            System.out.println("✅ Sample data inserted into 'users' table!");

            conn.close();

        
            conn.close();
        } catch (ClassNotFoundException e) {
            System.out.println("❌ MySQL JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("❌ Connection failed!");
            e.printStackTrace();
        }

         try (ClientSession basexSession = new ClientSession(basexHost, basexPort, basexUser, basexPass)) {
            System.out.println("✅ Connected to BaseX successfully!");
            String info = basexSession.execute("info");
            System.out.println("BaseX Server Info:\n" + info);
        } catch (Exception e) {
            System.out.println("❌ BaseX Connection failed!");
            e.printStackTrace();
        }

    }
}

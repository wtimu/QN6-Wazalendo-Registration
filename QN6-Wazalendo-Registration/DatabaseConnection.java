import java.sql.*;

public class DatabaseConnection {
   
    private static final String URL = "jdbc:ucanaccess://C:/Users/Tim/Desktop/QN6-Wazalendo-Registration/wazalendo.accdb";
    private static Connection connection = null;
    
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
                connection = DriverManager.getConnection(URL);
                System.out.println("Database connection established successfully.");
            } catch (ClassNotFoundException e) {
                throw new SQLException("Database driver not found. Make sure ucanaccess.jar is in lib folder.", e);
            }
        }
        return connection;
    }
    
    
    public static void createMembersTableIfNotExists() throws SQLException {
        
        DatabaseMetaData metaData = getConnection().getMetaData();
        ResultSet tables = metaData.getTables(null, null, "Members", null);
        
        if (tables.next()) {
            System.out.println("Table 'Members' already exists.");
            tables.close();
            return;
        }
        tables.close();
        
     
        String createTableSQL = "CREATE TABLE Members (" +
                "member_id TEXT(20) PRIMARY KEY, " +
                "full_name TEXT(100) NOT NULL, " +
                "nin TEXT(14) NOT NULL UNIQUE, " +
                "phone TEXT(10) NOT NULL, " +
                "initial_deposit CURRENCY NOT NULL, " +
                "registration_date DATETIME DEFAULT NOW())";
        
        try (Statement stmt = getConnection().createStatement()) {
            stmt.executeUpdate(createTableSQL);
            System.out.println("Table 'Members' created successfully.");
        }
    }
    
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}
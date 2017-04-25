/**
    RallyMe
    CSCI 4300, CRN 41126, Group 5

    rallyme.core.Database
 */

package rallyme.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private static final String CONNECTION_URL = "jdbc:mysql://localhost:3306/rallyme?serverTimezone=UTC";
    private static final String CONNECTION_USER = "rallyme";
    private static final String CONNECTION_PASS = "admin";
    private static Connection conn = null;

    public static Connection getConnection() {
        if(conn == null) {
            // Driver must be registered manually when loaded from WEB-INF/lib
            try {
                DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
                conn = DriverManager.getConnection(CONNECTION_URL, CONNECTION_USER, CONNECTION_PASS);
            } catch(SQLException ex) {
                throw new RuntimeException(ex);
            }
        }

        return conn;
    }
    
    
        
}

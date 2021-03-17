package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBConn {

    protected Connection conn;

    public DBConn() {
    }

    public void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Properties p = new Properties();
            p.put("user", "root");
            p.put("password", "root");
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1/roddi?"
            +"allowPublicKeyRetrieval=true&autoReconnect=true&useSSL=false", p);
        }
        catch (Exception e) {
                throw new RuntimeException("Unable to connect", e);
            
        }
    }
    public static void main(String[] args) {
        DBConn test = new DBConn();
        test.connect();

    }
}



import java.sql.*;

public class TestDB {
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
    "jdbc:mysql://localhost:3306/attendance_db?allowPublicKeyRetrieval=true&useSSL=false",
    "root",
    "bhakti15"
);

            System.out.println("Connected successfully!");
            con.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}


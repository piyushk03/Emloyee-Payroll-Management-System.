import java.sql.Connection;
import java.sql.DriverManager;

public class JDBC {
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/payrollmanagement", "root", "");
            if (con != null) {
                System.out.println("Successfully connected");
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;

public class AddStudentServlet extends HttpServlet {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/attendance_db?allowPublicKeyRetrieval=true&useSSL=false";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "bhakti15";

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String name = req.getParameter("name");
        String roll = req.getParameter("roll");
        String className = req.getParameter("className");
        String username = req.getParameter("username"); // optional mapping to users table

        try (PrintWriter out = resp.getWriter()) {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection con = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS)) {
                String sql = "INSERT INTO students (name, roll_no, className, username) VALUES (?, ?, ?, ?)";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, name);
                    ps.setString(2, roll);
                    ps.setString(3, className);
                    ps.setString(4, username);
                    ps.executeUpdate();
                }
            }
            // after adding redirect back to admin dashboard
            resp.sendRedirect("admin_dashboard.jsp");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}

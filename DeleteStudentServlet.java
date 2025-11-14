import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;

public class DeleteStudentServlet extends HttpServlet {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/attendance_db?allowPublicKeyRetrieval=true&useSSL=false";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "bhakti15";

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr = req.getParameter("id");
        if (idStr == null) {
            resp.sendRedirect("admin_dashboard.jsp");
            return;
        }
        int id = Integer.parseInt(idStr);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection con = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS)) {
                // delete attendance rows first to avoid FK issues
                try (PreparedStatement ps = con.prepareStatement("DELETE FROM attendance WHERE student_id = ?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps2 = con.prepareStatement("DELETE FROM students WHERE id = ?")) {
                    ps2.setInt(1, id);
                    ps2.executeUpdate();
                }
            }
            resp.sendRedirect("admin_dashboard.jsp");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}

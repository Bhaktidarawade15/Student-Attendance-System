import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;

public class LoginServlet extends HttpServlet {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/attendance_db?allowPublicKeyRetrieval=true&useSSL=false";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "bhakti15";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection con = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS)) {

                String sql = "SELECT role FROM users WHERE username=? AND password=?";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, username);
                    ps.setString(2, password);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            String role = rs.getString("role");
                            HttpSession session = request.getSession();
                            session.setAttribute("username", username);
                            session.setAttribute("role", role);

                            if ("admin".equals(role)) {
                                response.sendRedirect("admin.jsp");
                                return;
                            } else if ("teacher".equals(role)) {
                                response.sendRedirect("teacher.jsp");
                                return;
                            } else if ("student".equals(role)) {
                                // get student's roll_no from students table (students.username must be populated)
                                String roll = "";
                                String q = "SELECT roll_no FROM students WHERE username=?";
                                try (PreparedStatement psRoll = con.prepareStatement(q)) {
                                    psRoll.setString(1, username);
                                    try (ResultSet rsRoll = psRoll.executeQuery()) {
                                        if (rsRoll.next()) roll = rsRoll.getString("roll_no");
                                    }
                                }
                                response.sendRedirect("student.jsp?roll=" + (roll == null ? "" : roll));
                                return;
                            }
                        } else {
                            out.println("<h3>Invalid username or password</h3>");
                            out.println("<a href='login.html'>Try again</a>");
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}

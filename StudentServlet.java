import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;

public class StudentServlet extends HttpServlet {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/attendance_db?allowPublicKeyRetrieval=true&useSSL=false";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "bhakti15";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        try (PrintWriter out = response.getWriter()) {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection con = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS)) {

                if ("add".equals(action)) {
                    String name = request.getParameter("name");
                    String roll = request.getParameter("roll");
                    String className = request.getParameter("className");
                    String username = request.getParameter("username"); // map to users.username

                    String sql = "INSERT INTO students (name, roll_no, className, username) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement ps = con.prepareStatement(sql)) {
                        ps.setString(1, name);
                        ps.setString(2, roll);
                        ps.setString(3, className);
                        ps.setString(4, username);
                        int r = ps.executeUpdate();
                        if (r > 0) out.println("Student added.");
                        else out.println("Failed to add student.");
                    }
                    response.setHeader("Refresh", "1; URL=admin.jsp");
                    return;
                }

                if ("update".equals(action)) {
                    int id = Integer.parseInt(request.getParameter("id"));
                    String name = request.getParameter("name");
                    String roll = request.getParameter("roll");
                    String className = request.getParameter("className");
                    String username = request.getParameter("username");

                    String sql = "UPDATE students SET name=?, roll_no=?, className=?, username=? WHERE id=?";
                    try (PreparedStatement ps = con.prepareStatement(sql)) {
                        ps.setString(1, name);
                        ps.setString(2, roll);
                        ps.setString(3, className);
                        ps.setString(4, username);
                        ps.setInt(5, id);
                        int r = ps.executeUpdate();
                        if (r > 0) out.println("Student updated.");
                        else out.println("Update failed.");
                    }
                    response.setHeader("Refresh", "1; URL=admin.jsp");
                    return;
                }

                // default: unknown action
                out.println("Unknown action");
                response.setHeader("Refresh", "1; URL=admin.jsp");
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    // handle delete via GET: StudentServlet?action=delete&id=#
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (!"delete".equals(action)) {
            response.sendRedirect("admin.jsp");
            return;
        }

        String idStr = request.getParameter("id");
        if (idStr == null) {
            response.sendRedirect("admin.jsp");
            return;
        }

        int id = Integer.parseInt(idStr);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection con = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS)) {
                // delete attendance for student first (optional cascade)
                try (PreparedStatement ps = con.prepareStatement("DELETE FROM attendance WHERE student_id = ?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps2 = con.prepareStatement("DELETE FROM students WHERE id = ?")) {
                    ps2.setInt(1, id);
                    ps2.executeUpdate();
                }
            }
            response.sendRedirect("admin.jsp");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}

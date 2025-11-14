import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.time.LocalDate;

public class MarkAttendanceServlet extends HttpServlet {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/attendance_db?allowPublicKeyRetrieval=true&useSSL=false";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "bhakti15";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection con = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS)) {

                if ("markAttendance".equals(action)) {
                    // iterate student ids from DB and pick up status_<id>
                    try (Statement st = con.createStatement();
                         ResultSet rs = st.executeQuery("SELECT id FROM students")) {

                        LocalDate today = LocalDate.now();
                        while (rs.next()) {
                            int id = rs.getInt("id");
                            String status = request.getParameter("status_" + id);
                            if (status != null) {
                                // Either insert or update single record per date:
                                String checkSql = "SELECT id FROM attendance WHERE student_id = ? AND date = ?";
                                try (PreparedStatement check = con.prepareStatement(checkSql)) {
                                    check.setInt(1, id);
                                    check.setDate(2, java.sql.Date.valueOf(today));
                                    try (ResultSet rsCheck = check.executeQuery()) {
                                        if (rsCheck.next()) {
                                            // update
                                            try (PreparedStatement upd = con.prepareStatement(
                                                    "UPDATE attendance SET status=? WHERE student_id=? AND date=?")) {
                                                upd.setString(1, status);
                                                upd.setInt(2, id);
                                                upd.setDate(3, java.sql.Date.valueOf(today));
                                                upd.executeUpdate();
                                            }
                                        } else {
                                            // insert
                                            try (PreparedStatement ins = con.prepareStatement(
                                                    "INSERT INTO attendance (student_id, date, status) VALUES (?, ?, ?)")) {
                                                ins.setInt(1, id);
                                                ins.setDate(2, java.sql.Date.valueOf(today));
                                                ins.setString(3, status);
                                                ins.executeUpdate();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    out.println("<h3>✅ Attendance saved.</h3>");
                    response.setHeader("Refresh", "1; URL=teacher.jsp");
                    return;
                } else {
                    out.println("Unknown action");
                }
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}

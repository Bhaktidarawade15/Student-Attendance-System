<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%
    if (session == null || session.getAttribute("username") == null) {
        response.sendRedirect("login.html");
        return;
    }

    String role = (String) session.getAttribute("role");
    if (!"student".equals(role)) {
        response.sendRedirect("unauthorized.jsp");
        return;
    }
%>

<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Student Dashboard</title>
  <link rel="stylesheet" href="styles.css">
</head>
<body class="admin-page">

  <!-- Header -->
  <div class="login-container admin-container">
    <div class="login-header">
      <h2>Student Dashboard</h2>
      <a href="LogoutServlet" class="logout-btn">Logout</a>
    </div>

    <!-- Attendance Card -->
    <div class="login-card">
      <h3>Your Attendance Record</h3>
      <table class="styled-table">
        <thead>
          <tr>
            <th>Name</th>
            <th>Date</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
        <%
          String roll = request.getParameter("roll"); // <-- get from request (like before)
          if (roll == null) {
              out.println("<tr><td colspan='3' class='small'>No roll number provided.</td></tr>");
          } else {
              try {
                  Class.forName("com.mysql.cj.jdbc.Driver");
                  Connection con = DriverManager.getConnection(
                      "jdbc:mysql://localhost:3306/attendance_db?allowPublicKeyRetrieval=true&useSSL=false",
                      "root",
                      "bhakti15"
                  );
                  PreparedStatement ps = con.prepareStatement(
                    "SELECT s.name, a.date, a.status " +
                    "FROM students s " +
                    "JOIN attendance a ON s.id = a.student_id " +
                    "WHERE s.roll_no=? ORDER BY a.date DESC"
                  );
                  ps.setString(1, roll);
                  ResultSet rs = ps.executeQuery();

                  boolean hasRecords = false;
                  while (rs.next()) {
                      hasRecords = true;
        %>
          <tr>
            <td><%= rs.getString("name") %></td>
            <td><%= rs.getDate("date") %></td>
            <td><%= rs.getString("status") %></td>
          </tr>
        <%
                  }
                  if (!hasRecords) {
                      out.println("<tr><td colspan='3' class='small'>No attendance records found.</td></tr>");
                  }

                  rs.close(); ps.close(); con.close();
              } catch (Exception e) {
                  out.println("<tr><td colspan='3' class='small'>Error: " + e.getMessage() + "</td></tr>");
              }
          }
        %>
        </tbody>
      </table>
    </div>
  </div>

</body>
</html>

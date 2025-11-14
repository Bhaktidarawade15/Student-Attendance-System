<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%
    if (session == null || session.getAttribute("username") == null) {
        response.sendRedirect("login.html");
        return;
    }

    String role = (String) session.getAttribute("role");
    if (!"teacher".equals(role)) {
        response.sendRedirect("unauthorized.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Teacher Dashboard</title>
  <link rel="stylesheet" href="styles.css">
</head>
<body class="admin-page">

  <!-- container centers everything for admin/teacher pages -->
  <div class="login-container admin-container">

    <div class="login-form-wrapper">

      <!-- Header (title + logout) -->
      <div class="login-header">
        <h2>Teacher Dashboard - Mark Attendance</h2>
        <a href="LogoutServlet" class="logout-btn">Logout</a>
      </div>

      <!-- Card that holds the table/form -->
      <div class="login-card">
        <h3>Mark Attendance</h3>

        <form action="MarkAttendanceServlet" method="post" class="form-inline">
          <input type="hidden" name="action" value="markAttendance">

          <table class="styled-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Roll No</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
            <%
              try {
                  Class.forName("com.mysql.cj.jdbc.Driver");
                  Connection con = DriverManager.getConnection(
                      "jdbc:mysql://localhost:3306/attendance_db?allowPublicKeyRetrieval=true&useSSL=false",
                      "root",
                      "bhakti15"
                  );
                  Statement st = con.createStatement();
                  ResultSet rs = st.executeQuery("SELECT id, name, roll_no FROM students ORDER BY name");
                  while (rs.next()) {
                      int id = rs.getInt("id");
            %>
              <tr>
                <td><%= rs.getString("name") %></td>
                <td><%= rs.getString("roll_no") %></td>
                <td>
                  <select name="status_<%= id %>">
                    <option value="Present">Present</option>
                    <option value="Absent">Absent</option>
                  </select>
                </td>
              </tr>
            <%
                  }
                  rs.close(); st.close(); con.close();
              } catch (Exception e) {
                  out.println("<tr><td colspan='3' class='small'>Error: "+e.getMessage()+"</td></tr>");
              }
            %>
            </tbody>
          </table>

          <div style="text-align:right; margin-top:12px;">
            <button type="submit" class="dashboard-btn">Submit Attendance</button>
          </div>
        </form>

        <p class="small" style="margin-top:10px; color:#666;">
          Note: marking again for the same date will update the record.
        </p>
      </div>

    </div>
  </div>

</body>
</html>

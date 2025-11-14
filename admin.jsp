<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%
    if (session == null || session.getAttribute("username") == null) {
        response.sendRedirect("login.html");
        return;
    }
    String role = (String) session.getAttribute("role");
    if (!"admin".equals(role)) {
        response.sendRedirect("unauthorized.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Admin Dashboard</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body class="admin-page">
<div class="login-container">
    <div class="login-form-wrapper">
        <!-- Header with Logout -->
        <div class="login-header">
            <h2>Admin Dashboard</h2>
            <a href="LogoutServlet" class="logout-btn">Logout</a>
        </div>

        <!-- Add Student Form -->
        <div class="login-card">
            <h3>Add Student</h3>
            <form action="StudentServlet" method="post" class="form-inline">
                <input type="hidden" name="action" value="add">
                <input type="text" name="name" placeholder="Full Name" required>
                <input type="text" name="roll" placeholder="Roll No" required>
                <input type="text" name="className" placeholder="Class" required>
                <input type="text" name="username" placeholder="Username" required>
                <button type="submit" class="dashboard-btn">Add Student</button>
            </form>
        </div>

        <!-- Student List Table -->
        <div class="login-card">
            <h3>Student List</h3>
            <%
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection con = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/attendance_db?allowPublicKeyRetrieval=true&useSSL=false",
                        "root","bhakti15");
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery("SELECT id, name, roll_no, className, username FROM students ORDER BY id DESC");
            %>
            <table class="styled-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Roll</th>
                        <th>Class</th>
                        <th>Username</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                <%
                    while(rs.next()){
                %>
                    <tr>
                        <td><%= rs.getInt("id") %></td>
                        <td><%= rs.getString("name") %></td>
                        <td><%= rs.getString("roll_no") %></td>
                        <td><%= rs.getString("className") %></td>
                        <td><%= rs.getString("username") %></td>
                        <td>
                            <a href="admin_edit.jsp?id=<%= rs.getInt("id") %>" class="table-btn edit">Edit</a>
                            <a href="StudentServlet?action=delete&id=<%= rs.getInt("id") %>" class="table-btn delete" onclick="return confirm('Delete this student?')">Delete</a>
                        </td>
                    </tr>
                <%
                    }
                    rs.close();
                    st.close();
                    con.close();
                } catch(Exception e) {
                    out.println("<p class='small'>Error: "+e.getMessage()+"</p>");
                }
                %>
                </tbody>
            </table>
        </div>
    </div>
</div>
</body>
</html>

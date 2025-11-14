<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%
    // Session protection - only admin can access
   
    if (session == null || session.getAttribute("username") == null) {
        response.sendRedirect("login.html");
        return;
    }
    String role = (String) session.getAttribute("role");
    if (!"admin".equals(role)) {
        response.sendRedirect("unauthorized.jsp");
        return;
    }

    String idStr = request.getParameter("id");
    if (idStr == null) {
        out.println("<h3>Invalid student ID</h3>");
        return;
    }
    int studentId = Integer.parseInt(idStr);

    String name = "";
    String roll = "";
    String className = "";
    String username = "";

    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/attendance_db?allowPublicKeyRetrieval=true&useSSL=false",
            "root",
            "bhakti15"
        );
        PreparedStatement ps = con.prepareStatement(
            "SELECT name, roll_no, className, username FROM students WHERE id=?"
        );
        ps.setInt(1, studentId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            name = rs.getString("name");
            roll = rs.getString("roll_no");
            className = rs.getString("className");
            username = rs.getString("username");
        } else {
            out.println("<h3>Student not found</h3>");
            return;
        }
        rs.close();
        ps.close();
        con.close();
    } catch(Exception e) {
        out.println("Error: " + e.getMessage());
        return;
    }
%>

<html>
<head>
    <title>Edit Student</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>
<div class="container">
    <h2>Edit Student</h2>
    <form action="StudentServlet" method="post" class="form-inline">
        <input type="hidden" name="action" value="update">
        <input type="hidden" name="id" value="<%= studentId %>">

        <input type="text" name="name" placeholder="Full Name" value="<%= name %>" required>
        <input type="text" name="roll" placeholder="Roll No" value="<%= roll %>" required>
        <input type="text" name="className" placeholder="Class" value="<%= className %>" required>
        <input type="text" name="username" placeholder="Username" value="<%= username %>" required>

        <button type="submit">Update Student</button>
    </form>
    <br>
    <a href="admin_dashboard.jsp">← Back to Dashboard</a>
</div>
</body>
</html>

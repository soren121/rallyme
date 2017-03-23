package io.github.rallyme;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.io.PrintWriter;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import javax.servlet.http.HttpServlet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

@WebServlet(name="Login", urlPatterns={"/Login"})
public class Login extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection conn = null;
    private Configuration cfg;

    @Override
    public void init() {
         // Initialize FreeMarker
        cfg = new Configuration(Configuration.VERSION_2_3_25);
        cfg.setServletContextForTemplateLoading(getServletContext(), "/WEB-INF/templates");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);

        // Driver must be registered manually when loaded from WEB-INF/lib
        try {
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/rallyme", "rallyme", "admin");
        } catch(SQLException ex) { }
    }

    public void printError(PrintWriter outStream, String errorStr) throws IOException {
        Map<String, String> root = new HashMap<>();
        root.put("error", errorStr);

        try {
            Template tpl = cfg.getTemplate("login.ftl");
            tpl.process(root, outStream);
        } catch(TemplateException ex) {}
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, String> root = new HashMap<>();

        try {
            Template tpl = cfg.getTemplate("login.ftl");
            tpl.process(root, response.getWriter());
        } catch(TemplateException ex) {}
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, Object> root = new HashMap<>();
        Statement stmt = null;
        ResultSet rs = null;

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String passwordHashExisting = null;

        try {
            stmt = conn.createStatement();
            stmt.closeOnCompletion();
            rs = stmt.executeQuery("SELECT salt, password FROM users WHERE username = ? LIMIT 1");
        
            if(rs.next()) {
                passwordHashExisting = rs.getString("password");
            } else {
                printError(response.getWriter(), "Your username or password is incorrect.");
                return;
            }
        } catch (SQLException ex) {
            printError(response.getWriter(), "SQL exception: " + ex.getMessage());
            return;
        }

        if(PasswordHash.validatePassword(password.toCharArray(), passwordHashExisting)) {
            try {
                Template tpl = cfg.getTemplate("dashboard.ftl");
                tpl.process(root, response.getWriter());
            } catch(TemplateException ex) { }
        } else {
            printError(response.getWriter(), "Your username or password is incorrect.");
            return;
        }
    }

}

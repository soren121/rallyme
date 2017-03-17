package io.github.rallyme;

import java.io.PrintWriter;
import java.io.IOException;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

import javax.servlet.http.HttpServlet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// TODO: this is a boilerplate servlet template
// Please rename when this is something useful

@WebServlet(name="Servlet", urlPatterns={"/Servlet"})
public class ImdbResults extends HttpServlet {
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
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/rallyme", "demo", "demo");
        } catch(SQLException ex) { }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().println("here come dat boi");
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().println("o shit waddup");
    }

}

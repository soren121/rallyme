package rallyme.controller;

import rallyme.core.TemplateServlet;
import rallyme.model.Rally;
import rallyme.model.RallyType;
import rallyme.model.User;
import rallyme.exception.UserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.io.PrintWriter;
import java.sql.Timestamp;

import freemarker.template.Template;
import freemarker.template.TemplateException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name="AddRally", urlPatterns={"/AddRally"})
public class AddRally extends TemplateServlet {

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, Object> root = new HashMap<>();

        try {
            freemarker.getTemplate("addrally.ftl").process(root, response.getWriter());
        } catch(TemplateException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        
        String name = request.getParameter("name");
        Timestamp startTime = new Timestamp((new java.util.Date()).getTime());
        String location = request.getParameter("location");
        float latitude = Float.parseFloat(request.getParameter("latitude"));
        float longitude = Float.parseFloat(request.getParameter("longitude"));
        User creator = (rallyme.model.User) request.getSession().getAttribute("user");
        
        Rally newRally = new Rally(name, RallyType.LOCAL, startTime, location, latitude, longitude, creator);
        
        newRally.setDescription(request.getParameter("description"));
        newRally.setTwitterHandle(request.getParameter("twitterHandle"));
       //newRally.setEventCapacity(Integer.parseInt(request.getParameter("eventCapacity")));
        newRally.setUrl(request.getParameter("url"));
        newRally.save();
        
    }

}

/**
    RallyMe
    CSCI 4300, CRN 41126, Group 5

    rallyme.controller.AddRally
 */

package rallyme.controller;

import rallyme.core.TemplateServlet;
import rallyme.model.Rally;
import rallyme.model.RallyType;
import rallyme.model.User;
import rallyme.exception.RallyException;
import rallyme.exception.UserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.text.ParseException;

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
        User user = (User) request.getSession().getAttribute("user");
        
        Rally[] rallies;
        
        try {
            rallies = Rally.getAllNationalRallies();
        } catch (RallyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }
        
        root.put("rallylist", rallies);
        root.put("user", user);//for getting user firstname
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
        // Retrieve date/time fields
        String date = request.getParameter("date").trim();
        String time = request.getParameter("time").trim();
        // Parse date & time as a Date object
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
        long parsedStartTime = 0;
        try {
            parsedStartTime = dateFormatter.parse(date + " " + time).getTime();
        } catch(ParseException ex) {
            response.getWriter().println("Error parsing event time.");
            return;
        }

        // Retrieve form fields
        String id = request.getParameter("id");
        
        String name = request.getParameter("name");
        Timestamp startTime = new Timestamp(parsedStartTime);
        String location = request.getParameter("location");
        float latitude = Float.parseFloat(request.getParameter("latitude"));
        float longitude = Float.parseFloat(request.getParameter("longitude"));
        User creator = (rallyme.model.User) request.getSession().getAttribute("user");
        String twitterHandle = request.getParameter("twitterHandle").trim();
                        
        Rally newRally;
        // If we're editing a rally, update the data sent
        // Otherwise we're creating a new rally, so the ID should be set via auto-increment
        if(id != null) {
            newRally = new Rally(Integer.parseInt(id), name, RallyType.LOCAL, startTime, location, latitude, longitude, creator);
        } else {
            newRally = new Rally(name, RallyType.LOCAL, startTime, location, latitude, longitude, creator);
        }

        // Remove @handle from Twitter handle if included
        if(twitterHandle.charAt(0) == '@') {
            twitterHandle = twitterHandle.substring(1);
        }
        
        // Set optional fields in Rally objecet
        newRally.setDescription(request.getParameter("description"));
        newRally.setTwitterHandle(twitterHandle);
        //newRally.setEventCapacity(Integer.parseInt(request.getParameter("eventCapacity")));
        newRally.setUrl(request.getParameter("url"));
        
        // Set parent rally if specified
        try {
            newRally.setParent(Integer.parseInt(request.getParameter("parentRally")));
        } catch(RallyException ex) {}
        
        // Save rally to database
        newRally.save();
        
        // Redirect to the dashboard
        response.sendRedirect("Dashboard");
    }

}

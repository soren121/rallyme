/**
    RallyMe
    CSCI 4300, CRN 41126, Group 5

    rallyme.controller.AjaxRally
 */

package rallyme.controller;

import rallyme.core.FacebookEventSearch;
import rallyme.model.Rally;
import rallyme.model.User;
import rallyme.exception.FacebookEventException;
import rallyme.exception.RallyException;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

@WebServlet(name="AjaxRally", urlPatterns={"/AjaxRally"})
public class AjaxRally extends HttpServlet {

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String source = request.getParameter("source");

        if(source != null) {
            Rally[] ralliesArray;
            String jsonString = "{}";
            Gson gson = new Gson();

            User authUser = (User)request.getSession().getAttribute("user");
            String rallyId = request.getParameter("rally_id");
            float latitude = 0, longitude = 0;
            int radius = 0;

            if(rallyId == null) {
                latitude = Float.valueOf(request.getParameter("latitude")).floatValue();
                longitude = Float.valueOf(request.getParameter("longitude")).floatValue();
                radius = Integer.parseInt(request.getParameter("radius"));
            }

            if(source.equals("database")) {
                try {
                    if(rallyId == null) {
                        ralliesArray = (authUser != null) ? 
                            Rally.getRalliesByLocationAndUser(latitude, longitude, radius, authUser.getId()) :
                            Rally.getRalliesByLocation(latitude, longitude, radius);
                    } else {
                        ralliesArray = new Rally[] { 
                            Rally.getRallyById(Integer.parseInt(rallyId)) 
                        };
                    }
                    
                    jsonString = gson.toJson(ralliesArray);
                } catch (RallyException ex) {
                    ex.printStackTrace();
                }	
            } else if(source.equals("facebook")) {
                try {
                    FacebookEventSearch fbevents = new FacebookEventSearch(latitude, longitude, radius);
                    ralliesArray = fbevents.search();
                    jsonString = gson.toJson(ralliesArray);
                } catch(FacebookEventException ex) {
                    ex.printStackTrace();
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            response.setContentType("application/json");
            response.getWriter().print(jsonString);
        }
        
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

}

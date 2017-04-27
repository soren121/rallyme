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

/**
    This controller provides data in JSON format to the front page.
 */
@WebServlet(name="AjaxRally", urlPatterns={"/AjaxRally"})
public class AjaxRally extends HttpServlet {

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String source = request.getParameter("source");

        // Verify that a datasource was specified
        if(source != null) {
            Rally[] ralliesArray;
            String jsonString = "{}";
            Gson gson = new Gson();

            String rallyId = request.getParameter("rally_id");
            float latitude = 0, longitude = 0;
            int radius = 0;

            // If we aren't getting a particular rally, then we must be searching by location
            if(rallyId == null) {
                latitude = Float.valueOf(request.getParameter("latitude")).floatValue();
                longitude = Float.valueOf(request.getParameter("longitude")).floatValue();
                radius = Integer.parseInt(request.getParameter("radius"));
            }

            if(source.equals("database")) {
                try {
                    if(rallyId == null) {
                        // Search by location
                        ralliesArray = Rally.getRalliesByLocation(latitude, longitude, radius);
                    } else {
                        // Get a specific Rally, return as an array of one
                        ralliesArray = new Rally[] { 
                            Rally.getRallyById(Integer.parseInt(rallyId)) 
                        };
                    }
                    
                    // Convert array to JSON
                    jsonString = gson.toJson(ralliesArray);
                } catch (RallyException ex) {
                    ex.printStackTrace();
                }
            } else if(source.equals("facebook")) {
                try {
                    // Do a Facebook API search
                    FacebookEventSearch fbevents = new FacebookEventSearch(latitude, longitude, radius);
                    ralliesArray = fbevents.search();

                    // Convert returned array to JSON
                    jsonString = gson.toJson(ralliesArray);
                } catch(FacebookEventException ex) {
                    ex.printStackTrace();
                }
            } else {
                // No valid datasource = bad request
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            // Output JSON
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

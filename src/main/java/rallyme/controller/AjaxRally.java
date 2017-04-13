package rallyme.controller;

import rallyme.model.Rally;
import rallyme.model.User;
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
        return;
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if(action != null && action.equals("listRallies")) {
            Rally[] ralliesArray;
            
            try {
                float latitude = Float.valueOf(request.getParameter("latitude")).floatValue();
                float longitude = Float.valueOf(request.getParameter("longitude")).floatValue();
                int radius = Integer.parseInt(request.getParameter("radius"));

                ralliesArray = Rally.getRalliesByLocation(latitude, longitude, radius);

                Gson gson = new Gson();
                //convert Rally[] array to json string
                String jsonString = gson.toJson(ralliesArray);
                
                response.setContentType("application/json");
                response.getWriter().print(jsonString);
            } catch (RallyException ex) {
                ex.printStackTrace();
            }	
        }
    }

}

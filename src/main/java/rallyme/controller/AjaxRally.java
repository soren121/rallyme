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
    	
        
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if(action != null && action.equals("listRallies")) {
            Rally[] ralliesArray;
            String jsonString;
            
            try {
                String latitude = request.getParameter("latitude");
                String longitude = request.getParameter("longitude");
                latitude = latitude != null ? latitude : "0.0";
                longitude = longitude != null ? longitude : "0.0";

                ralliesArray = Rally.getAllRallies(Float.valueOf(latitude), Float.valueOf(longitude), 0);
                Gson gson = new Gson();
                jsonString = gson.toJson(ralliesArray); //convert Obj[] array to json string
                
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                out.print(jsonString);
                out.flush();
            } catch (RallyException ex) {
                ex.printStackTrace();
            }	
        }
    }

}

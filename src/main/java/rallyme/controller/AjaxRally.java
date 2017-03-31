package rallyme.controller;

import rallyme.core.TemplateServlet;
import rallyme.model.Rally;
import rallyme.model.User;
import rallyme.exception.RallyException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.io.PrintWriter;

import freemarker.template.Template;
import freemarker.template.TemplateException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

@WebServlet(name="AjaxRally", urlPatterns={"/AjaxRally"})
public class AjaxRally extends TemplateServlet {

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		System.out.println("Started Ajax Controller");

    	
        Rally[] ralliesArray;
        String jsonString;
		
		try {
			ralliesArray = Rally.getAllRallies();
			Gson gson = new Gson();
			jsonString = gson.toJson(ralliesArray); //convert Obj[] array to json string
	
			//request.setAttribute("products", ralliesArray); // Will be available as ${products} in JSP
			
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			out.print(jsonString);
			out.flush();

			//request.getRequestDispatcher("/index.html").forward(request, response);

		} catch (RallyException ex) {
			ex.printStackTrace();
		}
		
		System.out.println("Completed Ajax Controller");
		
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);		
    }

}

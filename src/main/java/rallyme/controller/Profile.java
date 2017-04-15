package rallyme.controller;

import rallyme.core.Database;
import rallyme.core.TemplateServlet;
import rallyme.model.Rally;
import rallyme.model.User;
import rallyme.exception.RallyException;
import rallyme.exception.UserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import freemarker.template.Template;
import freemarker.template.TemplateException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.mindrot.jbcrypt.BCrypt;

@WebServlet(name="Profile", urlPatterns={"/Profile"})
public class Profile extends TemplateServlet {

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    	Map<String, Object> root = new HashMap<>();

        User user = (User) request.getSession().getAttribute("user");
        /*String firstname = user.getFirstName();
        String lastname = user.getLastName();
        String email = user.getEmail();
        */
        
        root.put("user", user);
        
        try {
            freemarker.getTemplate("editprofile.ftl").process(root, response.getWriter());
        } catch(TemplateException ex) {
            throw new RuntimeException(ex);
        }
        
    }
        	
    	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            
            User user = (User) request.getSession().getAttribute("user");
            
        	boolean isMatch = User.validatePassword(user.getId(), request.getParameter("CurrentPassword"));
        	if(!isMatch){
        		Map<String, Object> root = new HashMap<>();
                root.put("error", "Current Password doesn't match.");
                
                //print out template
                try {
                    freemarker.getTemplate("editprofile.ftl").process(root, response.getWriter());
                } catch(TemplateException ex2) {
                    throw new RuntimeException(ex2);
                }
                //add return
                return ;
        	}
        	
            Connection conn = Database.getConnection();
            
            //for password
        	if(request.getParameter("NewP").equals(""))
        	{
        		//Skip the checking step, DO nothing
        	}
        	else if((!request.getParameter("NewP").equals(request.getParameter("CNewP"))))
        	{
                Map<String, Object> root = new HashMap<>();
                root.put("error", "New Password doesn't match the Comfirm New Password.");
        	}else if(request.getParameter("NewP").equals(request.getParameter("CNewP"))){
        		try{
            		String query = "update users set password = ? where id = ?";
            		PreparedStatement preparedStmt = conn.prepareStatement(query);  
                    String passwordHash = BCrypt.hashpw(request.getParameter("NewP"), BCrypt.gensalt());            		
                    preparedStmt.setString(1, passwordHash);
                 // execute the java preparedstatement
                    preparedStmt.executeUpdate();
        		}catch (SQLException e)
                {
                    System.err.println("Got an exception! ");
                    System.err.println(e.getMessage());
                  }	
        	}
        	
            try
            {
              // create the java mysql update preparedstatement
              String query = "update users set first_name = ?, last_name = ?, email = ? username = ? where id = ?";
              PreparedStatement preparedStmt = conn.prepareStatement(query);
              preparedStmt.setString(1, request.getParameter("Fname"));
              preparedStmt.setString(2, request.getParameter("Lname"));
              preparedStmt.setString(3, request.getParameter("Ename"));
              preparedStmt.setString(4, request.getParameter("Uname"));

              // execute the java preparedstatement
              preparedStmt.executeUpdate();
              
              conn.close();
            }
            catch (SQLException e)
            {
              System.err.println("Got an exception! ");
              System.err.println(e.getMessage());
            }	
            
            //login
            String userName = request.getParameter("username");
            String password = request.getParameter("password");

            try {
                // Attempt to log user in
                User user = User.login(userName, password);
                // Store User object in session
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                session.setMaxInactiveInterval(60 * 30);
                // Redirect to dashboard
                response.sendRedirect("Dashboard");
            } catch(UserException ex) {
                Map<String, Object> root = new HashMap<>();
                root.put("error", ex.getMessage());

                try {
                    freemarker.getTemplate("login.ftl").process(root, response.getWriter());
                } catch(TemplateException ex2) {
                    throw new RuntimeException(ex2);
                }
            }
            
            
        }   
}
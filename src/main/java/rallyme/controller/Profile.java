/**
    RallyMe
    CSCI 4300, CRN 41126, Group 5

    rallyme.controller.Profile
 */

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

/** 
   Profile page that allows user to view and edit their profile 
   and will refresh the page with all new information
   without sign the user out
 */
@WebServlet(name="Profile", urlPatterns={"/Profile"})
public class Profile extends TemplateServlet {

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    	//create a new user object
    	Map<String, Object> root = new HashMap<>();
        User user = (User) request.getSession().getAttribute("user");
        root.put("user", user);//for getting user information
        
        //bring up the template file for profile page
        try {
            freemarker.getTemplate("editprofile.ftl").process(root, response.getWriter());
        } catch(TemplateException ex) {
            throw new RuntimeException(ex);
        }
        
    }
        	
    	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            
    		//over world user object
    		Map<String, Object> root = new HashMap<>();
            User user = (User) request.getSession().getAttribute("user");
            root.put("user", user);
            
            //call the password checking function in User.java file
            //if the input value of current password doesn't match the database's password, 
            //reload the profile page without submitting any change and print the message
        	boolean isMatch = User.validatePassword(user.getId(), request.getParameter("CurP"));
        	if(!isMatch){
        		root.put("error", "Current Password doesn't match.");
                //print out template
                try {
                    freemarker.getTemplate("editprofile.ftl").process(root, response.getWriter());
                } catch(TemplateException ex2) {
                    throw new RuntimeException(ex2);
                }
                //add return
                return;
        	}
        	
        	//open the connection and won't close it until all of the changes are submitted.
            Connection conn = Database.getConnection();
            
            //comparing the NewPassword with Confirmed New Password
            if((!request.getParameter("NewP").equals(request.getParameter("CNewP"))))
        	{
            	//these two inputs have to be exactly the same, else 
                root.put("error", "New Password doesn't match the Comfirm New Password.");
                //if the current password is correct but the NewPassword is not equals to ComfirmNewPassword, reload the profile page
                try {
                    freemarker.getTemplate("editprofile.ftl").process(root, response.getWriter());
                } catch(TemplateException ex2) {
                    throw new RuntimeException(ex2);
                }
        	}else if(!request.getParameter("NewP").equals("") && request.getParameter("NewP").equals(request.getParameter("CNewP"))){
        		//only update the password if user has the input in the NewPassword text field, keep connection opening
        		try{
            		PreparedStatement preparedStmt = conn.prepareStatement(
            				"UPDATE users SET password = ? where id = ?;");  
                    String passwordHash = BCrypt.hashpw(request.getParameter("NewP"), BCrypt.gensalt());            		
                    preparedStmt.setString(1, passwordHash);
                    preparedStmt.setInt(2, user.getId());
                    preparedStmt.executeUpdate();
        		}catch (SQLException e)
                {
                    System.err.println("Got an exception! ");
                    System.err.println(e.getMessage());
                  }	
        	}
        	
        	//the edition that without the change of user's password
            try
            {
              // create the java mysql update preparedstatement
              PreparedStatement preparedStmt = conn.prepareStatement(
            		  "UPDATE users SET first_name = ?, last_name = ?, email = ?, username = ? where id = ?;");
              preparedStmt.setString(1, request.getParameter("Fname"));
              preparedStmt.setString(2, request.getParameter("Lname"));
              preparedStmt.setString(3, request.getParameter("Ename"));
              preparedStmt.setString(4, request.getParameter("Uname"));
              preparedStmt.setInt(5, user.getId());

              // execute the java preparedstatement
              preparedStmt.executeUpdate();
              
            }
            catch (SQLException e)
            {
              System.err.println("Got an exception! ");
              System.err.println(e.getMessage());
            }	
            
            //automatically log user in again with new profile information
            //doing the login without sign the user out
            String userName = request.getParameter("Uname");
            String password;
            //check if the user had update the password
            if(request.getParameter("NewP").equals(""))
        	{
                password = request.getParameter("CurP");
        	}else{
                password = request.getParameter("NewP");
        	}
            
            try {
                // Attempt to log user in again
                user = User.login(userName, password);
                root.put("user", user);
                // Store User object in session
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                session.setMaxInactiveInterval(60 * 30);
            } catch(UserException ex) {
                root.put("error", ex.getMessage());
                try {
                    freemarker.getTemplate("editprofile.ftl").process(root, response.getWriter());
                } catch(TemplateException ex2) {
                    throw new RuntimeException(ex2);
                }
            }
            
            //succeed with log use in again with new profile information, direct user to the profile page.
            try {
                freemarker.getTemplate("editprofile.ftl").process(root, response.getWriter());
            } catch(TemplateException ex2) {
                throw new RuntimeException(ex2);
            }
           
        }   
}

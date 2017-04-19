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
        
        root.put("user", user);
        
        try {
            freemarker.getTemplate("editprofile.ftl").process(root, response.getWriter());
        } catch(TemplateException ex) {
            throw new RuntimeException(ex);
        }
        
    }
        	
    	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            
    		Map<String, Object> root = new HashMap<>();
            User user = (User) request.getSession().getAttribute("user");
            root.put("user", user);
            
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
        	
            Connection conn = Database.getConnection();
            
            //comparing the NewPassword with Current New Password
            if((!request.getParameter("NewP").equals(request.getParameter("CNewP"))))
        	{
        		//doesn't match
                root.put("error", "New Password doesn't match the Comfirm New Password.");
        	}else if(!request.getParameter("NewP").equals("") && request.getParameter("NewP").equals(request.getParameter("CNewP"))){
        		try{
        			//matching success
            		PreparedStatement preparedStmt = conn.prepareStatement(
            				"UPDATE users SET password = ? where id = ?;");  
                    String passwordHash = BCrypt.hashpw(request.getParameter("NewP"), BCrypt.gensalt());            		
                    preparedStmt.setString(1, passwordHash);
                    preparedStmt.setInt(2, user.getId());
                 // execute the java preparedstatement
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
            
            //login
            String userName = request.getParameter("Uname");
            String password;
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
            try {
                freemarker.getTemplate("editprofile.ftl").process(root, response.getWriter());
            } catch(TemplateException ex2) {
                throw new RuntimeException(ex2);
            }
           
        }   
}
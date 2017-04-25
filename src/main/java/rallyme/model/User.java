/**
    RallyMe
    CSCI 4300, CRN 41126, Group 5

    rallyme.model.User
 */

package rallyme.model;

import rallyme.exception.UserException;
import rallyme.core.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;

/**
    Represents a logged in user.
    To instantiate this class, call User.login or User.register.
 */
public class User {

    private int id;
    private String userName;
    private String firstName;
    private String lastName;
    private String email;

    private User(int id, String userName, String email, String firstName, String lastName) {
        this.id = id;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public int getId() {
        return this.id;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getEmail() {
        return this.email;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getDisplayName() {
        return this.firstName + " " + this.lastName;
    }

    /**
         Log in a user.

         @param userName The user's username.
         @param password The user's password.

         @return A User object for this user.
         @throws UserException if there was an error while logging in the user.
     */
    public static User login(String userName, String password) throws UserException {
        Connection conn = Database.getConnection();
        String existingPasswordHash = null;

        int id;
        String firstName;
        String lastName;
        String email;

        try {
            // Find user in database, search by username
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ? LIMIT 1");
            stmt.setString(1, userName);
            ResultSet rs = stmt.executeQuery();

            // Get user info
            if(rs.next()) {
                existingPasswordHash = rs.getString("password");
                id = rs.getInt("id");
                firstName = rs.getString("first_name");
                lastName = rs.getString("last_name");
                email = rs.getString("email");
            } else {
                throw new UserException("Your username or password is incorrect.");
            }
        } catch(SQLException | NullPointerException ex) {
            throw new UserException("SQL exception: " + ex.getMessage());
        }

        // Validate password using the hash stored in the database
        if(BCrypt.checkpw(password, existingPasswordHash)) {
            // Return a User object
            return new User(id, userName, email, firstName, lastName);
        } else {
            throw new UserException("Your username or password is incorrect.");
        }
    }

    /**
         Register a new user.

         @param userName The user's username.
         @param password The user's password.
         @param email The user's email address.
         @param firstName The user's first name.
         @param lastName The user's last name.

         @return A User object for this user.
         @throws UserException if there was an error while registering the user.
     */
    public static User register(String userName, String password, String email, String firstName, String lastName) throws UserException {
        Connection conn = Database.getConnection();
        int id = 0;
        int result = -1;

        // Hash password with bcrypt
        String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());

        // Attempt to insert user
        try {
            PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO users (username, password, email, first_name, last_name)" +
                "VALUES(?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, userName);
            stmt.setString(2, passwordHash);
            stmt.setString(3, email);
            stmt.setString(4, firstName);
            stmt.setString(5, lastName);
            
            // Execute query
            result = stmt.executeUpdate();
            // Request auto-increment column
            ResultSet rs = stmt.getGeneratedKeys();

             // Get auto-increment ID
            if(rs.next()) {
                id = rs.getInt(1);
            }
        } catch(SQLException ex) {
            // MySQL error 1062 indicates a duplicate entry
            if(ex.getErrorCode() == 1062) {
                throw new UserException("That username is already taken. Please select a different username.");
            } else {
                throw new UserException("SQL exception: " + ex.getMessage());
            }
        }

        // If user was added successfully, the result should be 1 new row
        if(result == 1 && id > 0) {
            return new User(id, userName, email, firstName, lastName);
        } else {
            throw new UserException("An unknown error occurred.");
        }
    }

    /**
        Get a user object for a specific user by their ID.

        @param id The ID of the user to retrieve.
        @return A User object for that user.
     */
    public static User getUserById(int id) {
        Connection conn = Database.getConnection();
        String userName, firstName, lastName, email;

        // Attempt to get user
        try {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM users WHERE id = ?");
            stmt.setInt(1, id);
    
            // Execute query
            ResultSet rs = stmt.executeQuery();
            //iterate through results and add to list
            if(rs.next()) {
                userName = rs.getString("username");
                firstName = rs.getString("first_name");
                lastName = rs.getString("last_name");
                email = rs.getString("email");

                return new User(id, userName, email, firstName, lastName);
            } else {
                return null;
            }
        } catch(SQLException ex) {
        	return null;
        }
    }

    /**
        Validates the password of a specific user, given their ID and a plaintext password to check.

        @param id The ID of the user to check.
        @param password The plaintext password to validate.
        @return True if the password is correct, false if not.
     */
    public static boolean validatePassword(int id, String password) {
        Connection conn = Database.getConnection();
        String existingPasswordHash = null;

        try {
            // Find user in database, search by username
            PreparedStatement stmt = conn.prepareStatement("SELECT password FROM users WHERE id = ?");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            // Get user info
            if(rs.next()) {
                existingPasswordHash = rs.getString("password");
            } else {
                return false;
            }
        } catch(SQLException | NullPointerException ex) {
            throw new RuntimeException("SQL exception: " + ex.getMessage());
        }

        // Validate password using the hash stored in the database
        return BCrypt.checkpw(password, existingPasswordHash);
    }

}

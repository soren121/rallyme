package rallyme.model;

import rallyme.core.Database;
import rallyme.exception.UserException;

import java.sql.Connection;
import java.sql.PreparedStatement;
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

    // public static User register(String userName, String email, String firstName, String lastName) throws UserException {

    // }

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

}

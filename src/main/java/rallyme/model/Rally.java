/*************************************************
 * 
 * Created: 3/30/2017
 * 
 ************************************************/

package rallyme.model;

import rallyme.core.Database;
import rallyme.model.User;
import rallyme.exception.RallyException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Time;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import org.mindrot.jbcrypt.BCrypt;

public class Rally {
    
    private int id = 0;
    private String name;
    private RallyType type = RallyType.NATIONAL;
    private String description = "";
    private String twitterHandle;
    private String url;
    private Timestamp startTime;
    private String location = "";
    private float latitude;
    private float longitude;
    private User creator; // owner of rally
    private int eventCapacity = 100;
    
    /****************
     * Constructors
     ***************/
    public Rally(int id, String name, RallyType type, Timestamp startTime, String location, float latitude, float longitude, User creator){
        this.id = id;
        this.name = name;
        this.type = type;
        this.startTime = startTime;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.creator = creator;
    }
    
    public Rally(String name, RallyType type, Timestamp startTime, String location, float latitude, float longitude, User creator) {
        this.name = name;
        this.type = type;
        this.startTime = startTime;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.creator = creator;
    }
    
    /* Getters */
    public int getId(){
        return this.id;
    }

    public String getName() {
        return this.name;
    }
    
    public String getDescription() {
        return this.description;
    }

    public RallyType getType() {
        return this.type;
    }
    
    public String getTwitterHandle() {
        return this.twitterHandle;
    }

    public String getUrl() {
        return this.url;
    }
    
    public Timestamp getStartTime() {
        return this.startTime;
    }
    
    public String getClockTime(){
        java.util.Date date = this.startTime;        
        SimpleDateFormat timeformat = new SimpleDateFormat("h:mm a");
        return timeformat.format(date).toString();
    }
    
    public String getDateTime(){
        java.util.Date date = this.startTime;        
        SimpleDateFormat timeformat = new SimpleDateFormat("MM/dd/yyy");
        return timeformat.format(date).toString();
    }

    public String getLocation() {
        return this.location;
    }
    
    public float getLatitude() {
        return this.latitude;
    }
    
    public float getLongitude() {
        return this.longitude;
    }
    
    public User getCreator() {
        return this.creator;
    }

    public int getEventCapacity() {
        return this.eventCapacity;
    }

    /* Setters */
    public void setDescription(String description) {
        this.description = description;
    }

    public void setTwitterHandle(String handle) {
        this.twitterHandle = handle;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setEventCapacity(int eventCapacity) {
        this.eventCapacity = eventCapacity;
    }
    
    /**
        Saves a rally instance to the database.

        @return True on success.
        @throws RallyException if there is a fatal error.
     */
    public boolean save() { 
        Connection conn = Database.getConnection();
        int result = -1;
        
        // Attempt to insert user
        try {
            PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO rallies (creator_id, name, description, twitter_handle, url, start_time, location, latitude, longitude, event_capacity)" +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, this.creator.getId());
            stmt.setString(2, this.name);
            stmt.setString(3, this.description);
            stmt.setString(4, this.twitterHandle);
            stmt.setString(5, this.url);
            stmt.setTimestamp(6, this.startTime);
            stmt.setString(7, this.location);
            stmt.setFloat(8, this.latitude);
            stmt.setFloat(9, this.longitude);
            stmt.setInt(10, this.eventCapacity);
            
            // Execute query
            result = stmt.executeUpdate();
            // Request auto-increment column
            ResultSet rs = stmt.getGeneratedKeys();
             // Get auto-increment ID
            if(rs.next()) {
                this.id = rs.getInt(1);
            }
        } catch(SQLException ex) {
            //print error
            throw new RuntimeException("SQL exception: " + ex.getMessage());
        }

        return (result == 1);
    }

    /**
        Deletes a rally.

        @param id The ID of the rally.
        @return True on success.
        @throws RallyException if an error occurs.
    */ 
    public static boolean deleteRally(int id) throws RallyException {
        Connection conn = Database.getConnection();
        int result = -1;
        
        // Attempt to delete rally
        try {
            PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM rallies WHERE id = ?;");
            stmt.setInt(1, id);
           
            // Execute query
            result = stmt.executeUpdate();
          
        } catch(SQLException ex) {
            //print error
            throw new RallyException("SQL exception: " + ex.getMessage());
        }

        // If user was added successfully, the result should be 1 new row
        if(result == 1) {
            return true;
        } else {
            throw new RallyException("An unknown error adding new rally has occurred.");
        }
    }

    private static Rally[] getRallies(PreparedStatement pstmt) throws RallyException {
        ResultSet results;
        Vector<Rally> rallyList = new Vector<Rally>(); //used to create a String[] that is sent to next page and represented as table

        //iterate through results and add to list
        try {
            // Execute query
            results = pstmt.executeQuery();

            while(results.next()) {
                Rally rally = new Rally(
                    results.getInt("id"),
                    results.getString("name"),
                    RallyType.valueOf(results.getString("type").toUpperCase()),
                    results.getTimestamp("start_time"), 
                    results.getString("location"),
                    results.getFloat("latitude"),
                    results.getFloat("longitude"), 
                    User.getUserById(results.getInt("creator_id"))
                );

                rally.setDescription(results.getString("description"));
                rally.setTwitterHandle(results.getString("twitter_handle"));
                rally.setUrl(results.getString("url"));
                rally.setEventCapacity(results.getInt("event_capacity"));

                rallyList.add(rally);
            }
        } catch(SQLException ex) {
            throw new RallyException("SQL exception: " + ex.getMessage());
        }

        // return list in array form
        return (Rally[]) rallyList.toArray(new Rally[rallyList.size()]);
    }

    public static Rally[] getRalliesByUser(int creatorId) throws RallyException {
        Connection conn = Database.getConnection();
        PreparedStatement stmt;

        try {
            // Attempt to get rallies
            stmt = conn.prepareStatement(
                "SELECT * FROM rallies WHERE creator_id = ?;");

            // Set variables
            stmt.setInt(1, creatorId);
        } catch(SQLException ex) {
            throw new RallyException("SQL exception: " + ex.getMessage());
        }

        return Rally.getRallies(stmt);
    }
  
    /**
        Gets an array of all rallies in the database.

        @return An array of rally objects for all in database.
        @throws RallyException
    */
    public static Rally[] getRalliesByLocation(float latitude, float longitude, int radius) throws RallyException {
        Connection conn = Database.getConnection();
        PreparedStatement stmt;

        try {
            // Attempt to get rallies
            stmt = conn.prepareStatement(
                "SELECT *, " +
                "(3959 * acos(cos(radians(?)) * cos(radians(latitude)) *" + 
                "cos(radians(longitude) - radians(?)) +" +
                "sin(radians(?)) * sin(radians(latitude)))) AS distance" +
                " FROM rallies WHERE start_time > NOW() HAVING type = 'national' OR distance <= ?;");

            // Set variables
            stmt.setFloat(1, latitude);
            stmt.setFloat(2, longitude);
            stmt.setFloat(3, latitude);
            stmt.setInt(4, radius);
        } catch(SQLException ex) {
            throw new RallyException("SQL exception: " + ex.getMessage());
        }

        return Rally.getRallies(stmt);
    }
    
    /**
        Return 1 Rally from database and is retrieved with rally 'id'
    
        @return A Rally object from database.
        @throws RallyException
     */
    public static Rally getRallyById(String rallyId) throws RallyException {
        Connection conn = Database.getConnection();
        PreparedStatement stmt;

        // Attempt to get rally
        try {
            stmt = conn.prepareStatement("SELECT * FROM rallies WHERE id = ? LIMIT 1");
            stmt.setString(1, rallyId);
        } catch(SQLException ex) {
            throw new RallyException("SQL exception: " + ex.getMessage());
        }

        // return rally if found, otherwise return null
        Rally[] rallies = Rally.getRallies(stmt);
        return rallies[0];
    }
    
}

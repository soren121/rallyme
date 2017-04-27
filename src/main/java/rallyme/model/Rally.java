/**
    RallyMe
    CSCI 4300, CRN 41126, Group 5

    rallyme.model.Rally
 */

package rallyme.model;

import rallyme.core.Database;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;
import org.mindrot.jbcrypt.BCrypt;

/**
    Represents a Rally.
 */
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
    private RallyEntry parent = null;
    private RallyEntry[] sisters = new RallyEntry[0];
    
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

    public Rally(String name, RallyType type, Timestamp startTime, String location, float latitude, float longitude) {
        this.name = name;
        this.type = type;
        this.startTime = startTime;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.creator = null;
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
    
    public RallyEntry getParent() {
        return this.parent;
    }

    public RallyEntry[] getSisters() {
        return this.sisters;
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
        Set Rally parent name similar to other setters but works with input id
        and searches database

        @throws RallyException if there is a fatal error.
     */
    public void setParent(int parentId) throws RallyException{
         Connection conn = Database.getConnection();
         PreparedStatement stmt = null;
         
         try {
            stmt = conn.prepareStatement("SELECT name FROM rallies WHERE id = ?");
            stmt.setInt(1, parentId);
        } catch(SQLException e) {
            e.printStackTrace();
        }

        //iterate through results and add to list
        try {
            // Execute query
            ResultSet results = stmt.executeQuery();

            if(results.next()) {
                this.parent = new RallyEntry(
                    parentId,
                    results.getString("name")
                );
            }
        } catch(SQLException ex) {
            throw new RallyException("SQL exception: " + ex.getMessage());
        }
         
    }
    
    /**
        Saves a rally instance to the database.

        @return True on success.
        @throws RallyException if there is a fatal error.
     */
    public boolean save() { 
        Connection conn = Database.getConnection();
        int result = -1;
        PreparedStatement stmt;

        // Attempt to insert rally
        try {
            if(this.id > 0) {
                    stmt = conn.prepareStatement(
                    "INSERT INTO rallies (id, creator_id, name, type, start_time, location, latitude, longitude, description, twitter_handle, url, event_capacity, parent_id)" +
                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)" +
                    "ON DUPLICATE KEY UPDATE creator_id = VALUES(creator_id), name = VALUES(name), type = VALUES(type), start_time = VALUES(start_time), location = VALUES(location), " +
                    "latitude = VALUES(latitude), longitude = VALUES(longitude), description = VALUES(description), twitter_handle = VALUES(twitter_handle), " +
                    "url = VALUES(url), event_capacity = VALUES(event_capacity), parent_id = VALUES(parent_id)",
                    Statement.RETURN_GENERATED_KEYS);
               
                // Set required parameters
                stmt.setInt(1, this.id);
                stmt.setInt(2, this.creator != null ? this.creator.getId() : 0);
                stmt.setString(3, this.name);
                stmt.setString(4, this.type.name().toLowerCase());
                stmt.setTimestamp(5, this.startTime);
                stmt.setString(6, this.location);
                stmt.setFloat(7, this.latitude);
                stmt.setFloat(8, this.longitude);
                // Set optional parameters
                if(this.description != null) 
                    stmt.setString(9, this.description); 
                else 
                    stmt.setString(9, "");
                if(this.twitterHandle != null) 
                    stmt.setString(10, this.twitterHandle); 
                else 
                    stmt.setNull(10, java.sql.Types.VARCHAR);
                if(this.url != null) 
                    stmt.setString(11, this.url); 
                else 
                    stmt.setNull(11, java.sql.Types.VARCHAR);
                if(this.eventCapacity > 0) 
                    stmt.setInt(12, this.eventCapacity);
                else 
                    stmt.setInt(12, 0);
                if(this.parent != null && this.parent.getId() > 0)
                    stmt.setInt(13, this.parent.getId());
                else
                    stmt.setNull(13, java.sql.Types.VARCHAR);
            
            } else {
                stmt = conn.prepareStatement(
                    "INSERT INTO rallies (creator_id, name, type, start_time, location, latitude, longitude, description, twitter_handle, url, event_capacity, parent_id)" +
                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
                
                // Set required parameters
                stmt.setInt(1, this.creator != null ? this.creator.getId() : 0);
                stmt.setString(2, this.name);
                stmt.setString(3, this.type.name().toLowerCase());
                stmt.setTimestamp(4, this.startTime);
                stmt.setString(5, this.location);
                stmt.setFloat(6, this.latitude);
                stmt.setFloat(7, this.longitude);
                // Set optional parameters
                // Set optional parameters
                if(this.description != null) 
                    stmt.setString(8, this.description); 
                else 
                    stmt.setString(8, "");
                if(this.twitterHandle != null) 
                    stmt.setString(9, this.twitterHandle); 
                else 
                    stmt.setNull(9, java.sql.Types.VARCHAR);
                if(this.url != null) 
                    stmt.setString(10, this.url); 
                else 
                    stmt.setNull(10, java.sql.Types.VARCHAR);
                if(this.eventCapacity > 0) 
                    stmt.setInt(11, this.eventCapacity);
                else 
                    stmt.setInt(11, 0);
                if(this.parent != null && this.parent.getId() > 0)
                    stmt.setInt(12, this.parent.getId());
                else
                    stmt.setNull(12, java.sql.Types.VARCHAR);
               
            }
            
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

    /**
        Private method that retrieves fields and constructs an array of Rally objects,
        given a PreparedStatement that fetches rows from the rallies table.

        @param pstmt - An SQL query object that retrieves rally rows.
        @return An array of constructed Rally objects.
     */
    private static Rally[] getRallies(PreparedStatement pstmt) throws RallyException {
        ResultSet results;
        Vector<Rally> rallyList = new Vector<Rally>(); //used to create a String[] that is sent to next page and represented as table

        //iterate through results and add to list
        try {
            // Execute query
            results = pstmt.executeQuery();

            // Construct a new Rally for each row
            while(results.next()) {
                Rally rally = new Rally(
                    results.getInt("id"),
                    results.getString("name"),
                    RallyType.valueOf(results.getString("type").toUpperCase()),
                    results.getTimestamp("start_time"), 
                    results.getString("location"),
                    results.getFloat("latitude"),
                    results.getFloat("longitude"), 
                    results.getInt("creator_id") > 0 ? 
                        User.getUserById(results.getInt("creator_id")) : 
                        null
                );

                rally.setDescription(results.getString("description"));
                rally.setTwitterHandle(results.getString("twitter_handle"));
                rally.setUrl(results.getString("url"));
                rally.setEventCapacity(results.getInt("event_capacity"));

                // If parent_id is null (=0), discover any sister rallies
                // If not null, this is a sister rally, so set its parent
                int parentId = results.getInt("parent_id");
                if(parentId == 0) {
                    rally.setSisters(rally.getId());
                } else {
                    rally.setParent(parentId);
                }
                
                // Add object to Vector
                rallyList.add(rally);
            }
        } catch(SQLException ex) {
            throw new RallyException("SQL exception: " + ex.getMessage());
        }

        // return list in array form
        return (Rally[]) rallyList.toArray(new Rally[rallyList.size()]);
    }

    /**
        Gets an array of all rallies created by the given user.

        @return An array of rally objects.
        @throws RallyException
    */
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
        Gets an array of all rallies near the given location.

        @return An array of rally objects.
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
                " FROM rallies WHERE start_time >= CURDATE() HAVING type = 'national' OR distance <= ?;");

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
        Return one Rally from database given its ID.
    
        @param rallyId - The ID of the rally to fetch.
        @return A Rally object from database.
        @throws RallyException
     */
    public static Rally getRallyById(int rallyId) throws RallyException {
        Connection conn = Database.getConnection();
        PreparedStatement stmt;

        // Attempt to get rally
        try {
            stmt = conn.prepareStatement("SELECT * FROM rallies WHERE id = ? LIMIT 1");
            stmt.setInt(1, rallyId);
        } catch(SQLException ex) {
            throw new RallyException("SQL exception: " + ex.getMessage());
        }

        // return rally if found, otherwise return null
        Rally[] rallies = Rally.getRallies(stmt);
        return rallies[0];
    }
    
    /**
        Gets an array of all national rallies.

        @return An array of rally objects.
        @throws RallyException
    */
    public static Rally[] getAllNationalRallies() throws RallyException {
         Connection conn = Database.getConnection();
         PreparedStatement stmt;

         try {
             // Attempt to get rallies
             stmt = conn.prepareStatement("SELECT * FROM rallies WHERE type='national';");
 
         } catch(SQLException ex) {
             throw new RallyException("SQL exception: " + ex.getMessage());
         }
         return Rally.getRallies(stmt);
    }

    /**
        Attempts to discover sister rallies that may exist for this rally.
        If any are found, they are added as RallyEntry objects to the class sisters variable.

        @param parentId - The ID of the parent rally to check.
        @throws RallyException
    */
    private void setSisters(int parentId) throws RallyException {
         Connection conn = Database.getConnection();
         PreparedStatement stmt = null;
         
         try {
            stmt = conn.prepareStatement("SELECT id, name FROM rallies WHERE parent_id = ?");
            stmt.setInt(1, parentId);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        ArrayList<RallyEntry> sisters = new ArrayList<RallyEntry>();
        ResultSet results;
       
        //iterate through results and add to list
        try {
            // Execute query
            results = stmt.executeQuery();

            // Create a RallyEntry object for each sister rally found
            while(results.next()) {
                RallyEntry entry = new RallyEntry(
                    results.getInt("id"),
                    results.getString("name")
                );
                sisters.add(entry);
            }
        } catch(SQLException ex) {
            throw new RallyException("SQL exception: " + ex.getMessage());
        }
        
        // Set as a static array
        this.sisters = (RallyEntry[]) sisters.toArray(new RallyEntry[sisters.size()]);
    }
    
}

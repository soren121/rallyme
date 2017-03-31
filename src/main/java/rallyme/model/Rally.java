/*************************************************
 * 
 * Created: 3/30/2017
 * 
 ************************************************/

package rallyme.model;

import rallyme.core.Database;
import rallyme.exception.RallyException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.Vector;
import org.mindrot.jbcrypt.BCrypt;

public class Rally {
	
	private int id;
    private String name;
	private String description;
	private String twitterHandle;
	private Timestamp startTime;
	private float latitude;
	private float longitude;
	private int userId; // owner of rally
	
	/****************
	 * Constructors
	 ***************/
	public Rally(int id, String name, String description, String twitterHandle, Timestamp startTime, float latitude, float longitude, int userId){
		this.id = id;
        this.name = name;
		this.description = description;
        this.twitterHandle = twitterHandle;
		this.startTime = startTime;
		this.latitude = latitude;
		this.longitude = longitude;
		this.userId = userId;
	}
	
	public Rally(String name, String description, String twitterHandle, Timestamp startTime, float latitude, float longitude, int userId){
        this.name = name;
        this.description = description;
        this.twitterHandle = twitterHandle;
		this.startTime = startTime;
		this.latitude = latitude;
		this.longitude = longitude;
		this.userId = userId;
	}
	
	public Rally(){
		
	}
	
	/*************
	 * Getters
	 ************/
	public int getId(){
		return this.id;
	}
	
	public String getDescription(){
		return this.description;
	}
	
	public String getTwitterHandle(){
		return this.twitterHandle;
	}
	
	public Timestamp getstartTime(){
		return this.startTime;
	}
	
	public float getLatitude(){
		return this.latitude;
	}
	
	public float getLongitude(){
		return this.longitude;
	}
	
	public int getUserId(){
		return this.userId;
	}
	
	/***********************
	 * Rally Functions
	 **********************/
	
	/***************************************************************************
	 * addNewRally
	 * 
	 * @param description The details of rally.
     * @param twitterHandle The twitter handle associated with rally instance.
     * @param startTime DATETIME The stored in database time of event.
     * @param latitude The Geolocation of event.
     * @param longitude The Geolocaiton of event.
     * @param userId The id of the user creating the rally.
	 * 
	 * @return Rally object for this newly created rally.
	 * @throws UserException 
	 ****************************************************************************/ 
	public static Rally addNewRally(String name, String description, String twitterHandle, Timestamp startTime, float latitude, float longitude, int userId) throws RallyException { 
		Connection conn = Database.getConnection();
        int id = 0;
        int result = -1;
        
        // Attempt to insert user
        try {
            PreparedStatement stmt = conn.prepareStatement(
		                "INSERT INTO rallies (name, description, twitter_handle, start_time, latitude, longitude, user_id)" +
		                "VALUES(?, ?, ?, ?, ?, ?)",
		                Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.setString(3, twitterHandle);
            stmt.setTimestamp(4, startTime);
            stmt.setFloat(5, latitude);
            stmt.setFloat(6, longitude);
            stmt.setInt(7, userId);
            
            // Execute query
            result = stmt.executeUpdate();
            // Request auto-increment column
            ResultSet rs = stmt.getGeneratedKeys();
             // Get auto-increment ID
            if(rs.next()) {
                id = rs.getInt(1);
            }
        } catch(SQLException ex) {
        	//print error
        	throw new RallyException("SQL exception: " + ex.getMessage());
        }

        // If user was added successfully, the result should be 1 new row
        if(result == 1) {
            return new Rally(id, name, description, twitterHandle, startTime, latitude, longitude, userId);
        } else {
            throw new RallyException("An unknown error adding new rally has occurred.");
        }
	}
	
	/***********************
	 * deleteRally
	 * 
     * @param id The id of the rally.
	 * 
	 * @return Int return 1 success.
	 * @throws UserException
	 **********************/ 
	public static int deleteRally(int id) throws RallyException {
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
            return 1;
        } else {
            throw new RallyException("An unknown error adding new rally has occurred.");
        }
	}
	
	/***************************************************************************
	 * getAllRallies
	 *
	 * 
	 * @return Rally[] array of rally objects for all in database.
	 * @throws UserException 
	 ****************************************************************************/ 
	public static Rally[] getAllRallies() throws RallyException {
		Connection conn = Database.getConnection();
        ResultSet results;
	    Vector<Rally> rallyList = new Vector<Rally>(); //used to create a String[] that is sent to next page and represented as table

        // Attempt to delete rally
        try {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM rallies;");
    
            // Execute query
            results = stmt.executeQuery();
            //iterate through results and add to list
            while(results.next()) {
            	rallyList.add(new Rally(
                    results.getInt("id"),
                    results.getString("name"),
                    results.getString("description"),
                    results.getString("twitter_handle"),
                    results.getTimestamp("start_time"), 
                    results.getFloat("latitude"),
                    results.getFloat("longitude"), 
                    results.getInt("user_id")
                ));
            }
        } catch(SQLException ex) {
        	throw new RallyException("SQL exception: " + ex.getMessage());
        }
		return (Rally[]) rallyList.toArray(new Rally[rallyList.size()]); //return list in array form
	}
	
}

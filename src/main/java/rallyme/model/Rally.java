/*************************************************
 * 
 * Created: 3/30/2017
 * 
 ************************************************/

package rallyme.model;

import rallyme.core.Database;
import rallyme.exception.UserException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Date;
import java.util.Vector;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Rally {
	
	private int id;
	private String description;
	private String twitter_handle;
	private String start_time;		//DATETIME
	private float latitude;			//Geolocation
	private float longitude;
	private int user_id; 			//owner of rally
	
	/****************
	 * Constructors
	 ***************/
	public Rally(int id, String description, String twitter_handle, String start_time, float latitude, float longitude, int user_id){
		this.id = id;
		this.description = description;
		this.twitter_handle = twitter_handle;
		this.start_time = start_time;
		this.latitude = latitude;
		this.longitude = longitude;
		this.user_id = user_id;
	}
	
	public Rally(String description, String twitter_handle, String start_time, float latitude, float longitude, int user_id){
		this.description = description;
		this.twitter_handle = twitter_handle;
		this.start_time = start_time;
		this.latitude = latitude;
		this.longitude = longitude;
		this.user_id = user_id;
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
	
	public String getTwitter_handle(){
		return this.twitter_handle;
	}
	
	public String getStart_time(){
		return this.description;
	}
	
	public float getLatitude(){
		return this.latitude;
	}
	
	public float getLongitude(){
		return this.longitude;
	}
	
	public int getUser_id(){
		return this.user_id;
	}
	
	/***********************
	 * Rally Functions
	 **********************/
	
	/***************************************************************************
	 * addNewRally
	 * 
	 * @param description The details of rally.
     * @param twitter_handle The twitter handle associated with rally instance.
     * @param start_time DATETIME The stored in database time of event.
     * @param latitude The Geolocation of event.
     * @param longitude The Geolocaiton of event.
     * @param user_id The id of the user creating the rally.
	 * 
	 * @return Rally object for this newly created rally.
	 * @throws UserException 
	 ****************************************************************************/ 
	public static Rally addNewRally(String description, String twitter_handle, String start_time, float latitude, float longitude, int user_id) throws UserException{ 
		Connection conn = Database.getConnection();
        int id = 0;
        int result = -1;
        
        // Attempt to insert user
        try {
            PreparedStatement stmt = conn.prepareStatement(
		                "INSERT INTO rallies (description, twitter_handle, start_time, latitude, longitude, user_id)" +
		                "VALUES(?, ?, ?, ?, ?, ?)",
		                Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, description);
            stmt.setString(2, twitter_handle);
            stmt.setString(3, start_time);
            stmt.setString(4, String.valueOf(latitude));
            stmt.setString(5, String.valueOf(longitude));
            stmt.setString(6, String.valueOf(user_id));
            
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
        	throw new UserException("SQL exception: " + ex.getMessage());
        }

        // If user was added successfully, the result should be 1 new row
        if(result == 1) {
            return new Rally(id, description, twitter_handle, start_time, latitude, longitude, user_id);
        } else {
            throw new UserException("An unknown error adding new rally has occurred.");
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
	public static int deleteRally(int id) throws UserException {
		Connection conn = Database.getConnection();
        int result = -1;
        
        // Attempt to delete rally
        try {
            PreparedStatement stmt = conn.prepareStatement(
		                "DELETE FROM rallies WHERE id='?';",
		                Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, String.valueOf(id));
           
            // Execute query
            result = stmt.executeUpdate();
          
        } catch(SQLException ex) {
        	//print error
        	throw new UserException("SQL exception: " + ex.getMessage());
        }

        // If user was added successfully, the result should be 1 new row
        if(result == 1) {
            return 1;
        } else {
            throw new UserException("An unknown error adding new rally has occurred.");
        }
	}
	
	/***************************************************************************
	 * getAllRallies
	 *
	 * 
	 * @return Rally[] array of rally objects for all in database.
	 * @throws UserException 
	 ****************************************************************************/ 
	public static Rally[] getAllRallies() throws UserException{
		Connection conn = Database.getConnection();
        ResultSet results;
	    Vector<Rally> rallyList = new Vector<Rally>(); //used to create a String[] that is sent to next page and represented as table

        // Attempt to delete rally
        try {
            PreparedStatement stmt = conn.prepareStatement(
		                "SELECT id, description, twitter_handle, start_time, latitude, longitude, user_id  FROM rallies;",
		                Statement.RETURN_GENERATED_KEYS);
    
            // Execute query
            results = stmt.executeQuery();
            //iterate through results and add to list
            if(results.next()){
            	rallyList.add(new Rally(results.getInt(1), //id
            			results.getString(2), 			   //description
            			results.getString(3),			   //twitter_handle
            			results.getString(4),              //start_time
            			results.getFloat(5),               //latitude
            			results.getFloat(6),               //longitude
            			results.getInt(7)));               //user_id
            }
        } catch(SQLException ex) {
        	throw new UserException("SQL exception: " + ex.getMessage());
        }
		return (Rally[]) rallyList.toArray(new Rally[rallyList.size()]); //return list in array form
	}
	
}

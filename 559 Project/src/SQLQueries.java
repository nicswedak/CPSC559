import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class SQLQueries {

	
	public void viewUsers(Connection myConnection, String myDataBase) throws SQLException {

		    Statement statement = null;
		    String query = "select username, password from " + myDataBase + ".users";
		   
		    try {
		        statement = myConnection.createStatement();
		        ResultSet results = statement.executeQuery(query);
		        while(results.next()) {
		        	String userName = results.getString("username");
		        	String passWord = results.getString("password");
		            System.out.println(userName + "\t" + passWord);
		        }
		    } catch (SQLException e) {
		        System.out.println(e);
		    } finally {
		        if (statement != null) { 
		        	statement.close(); 
		        }
		    }
		}
	
	public void viewRatings(Connection myConnection, String myDataBase, String input) throws SQLException {

	    Statement statement = null;
	    String query = "select rating, classname, classnumber, comments from " + myDataBase 
	    		+ ".ratings where lname = '" + input + "'";
	   
	    try {
	        statement = myConnection.createStatement();
	        ResultSet results = statement.executeQuery(query);
	        while(results.next()) {
	        	//String fname = results.getString("fname");
	        	//String lname = results.getString("lname");
	        	int rating = results.getInt("rating");
	        	//String school = results.getString("school");
	        	String classname = results.getString("classname");
	        	int classnumber = results.getInt("classnumber");
	        	String comments = results.getString("comments");
	            System.out.println(rating + "\t" + classname + "\t" + classnumber + "\n" + comments);
	        }
	    } catch (SQLException e) {
	        System.out.println(e);
	    } finally {
	        if (statement != null) { 
	        	statement.close(); 
	        }
	    }
	}
	
public void addProf(Connection myConnection, String myDataBase, String fName, String lName, String school) throws SQLException {
		
		Statement statement = null;
		
		try {
			
			String query = "insert into " + myDataBase + ".profs (fname, lname, school) " + 
					"values ('" + fName + "', '" + lName + "', '" + school + "')";
			statement = myConnection.createStatement();
			statement.executeUpdate(query);
			
		} catch (SQLException e) {
	        System.out.println(e);
	    } finally {
	        if (statement != null) { 
	        	statement.close(); 
	        }
	    }
	}

public void removeProf(Connection myConnection, String myDataBase, String oldFName, String oldLName) throws SQLException {
	
	Statement statement = null;
	
	try {
		
		String query = "delete from " + myDataBase + ".profs where fname = '" 
		+ oldFName + "' and lname = '" + oldLName + "'"; 
		statement = myConnection.createStatement();
		statement.executeUpdate(query);
		
	} catch (SQLException e) {
        System.out.println(e);
    } finally {
        if (statement != null) { 
        	statement.close(); 
        }
    }
}

public void addRating(Connection myConnection, String myDataBase, String fName, String lName, 
		int rating, String className, int classNumber, String comments) throws SQLException {
	
	Statement statement = null;
	
	try {
		
		String query = "insert into " + myDataBase + ".ratings (fname, lname, rating, classname, classnumber, comments) " + 
				"values ('" + fName + "', '" + lName + "', '" + rating + "', '" + className + "', '" + classNumber + "', '" + comments + "')";
		statement = myConnection.createStatement();
		statement.executeUpdate(query);
		
	} catch (SQLException e) {
        System.out.println(e);
    } finally {
        if (statement != null) { 
        	statement.close(); 
        }
    }
}
}

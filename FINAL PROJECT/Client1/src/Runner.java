/*
 * Runner is the input equivalent class for a SQL Database connection
 */

import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Scanner;


public class Runner {

	public static void main(String[] args) {

		Scanner userInput = new Scanner(System.in);
		DBConnect connection = new DBConnect();
		SQLQueries queries = new SQLQueries();
		boolean loggedOut = false;

		do  {

			System.out.println("What would you like to do?");
			System.out.println("1: for a list of all users.");
			System.out.println("2: to search by a professors last name.");
			System.out.println("3: to add a new professor");
			System.out.println("4: to remove an existing professor");
			System.out.println("5: to add a rating for an existing professor");
			System.out.println("Logout to stop the program.");

			String input = userInput.nextLine();
			if(input.equals("1")){

				try {
					queries.viewUsers(connection.SQLConnection(), "ratemyprof");
				} catch (SQLException e) {
					e.printStackTrace();
				}

			} else if (input.equals("2")) {

				try {
					System.out.println("Enter the last name of the professor you wish to search");
					String myInput = userInput.nextLine();
					queries.viewRatings(connection.SQLConnection(), "ratemyprof", myInput);
				} catch (SQLException e) {
					e.printStackTrace();
				}

			} else if (input.equals("3")) {

				try {
					System.out.println("Enter the professors first name");
					String newFName = userInput.nextLine();
					System.out.println("Enter the professors last name");
					String newLName = userInput.nextLine();
					System.out.println("Enter the professors school");
					String newSchool = userInput.nextLine();

					queries.addProf(connection.SQLConnection(), "ratemyprof", newFName, newLName, newSchool);
				} catch (SQLException e) {
					e.printStackTrace();
				}

			} else if (input.equals("4")) {

				try {
					System.out.println("Enter the professors first name");
					String oldFName = userInput.nextLine();
					System.out.println("Enter the professors last name");
					String oldLName = userInput.nextLine();
					queries.removeProf(connection.SQLConnection(), "ratemyprof", oldFName, oldLName);

				} catch (SQLException e) {
					//Want to print this instead of the actual error
					System.out.println("Cannot remove that prof as they have valid ratings");
				}
			} else if (input.equals("5")) {

				try {

					System.out.println("Enter the professors first name");
					String oldFName = userInput.nextLine();
					System.out.println("Enter the professors last name");
					String oldLName = userInput.nextLine();
					System.out.println("Enter any comments you have for the professor");
					String comments = userInput.nextLine();
					System.out.println("Enter the 4 digit class name for the class the professor taught");
					String className = userInput.nextLine();
					System.out.println("Enter the class number for the class the professors taught");
					int classNumber = userInput.nextInt();
					System.out.println("Enter the professors rating");
					int rating = userInput.nextInt();
					userInput.nextLine();

					queries.addRating(connection.SQLConnection(), "ratemyprof", oldFName, oldLName, rating, className, classNumber, comments);

				} catch (SQLException e) {
					e.printStackTrace();
				}

			} else if (input.equals("Logout")) {
				loggedOut = true;
			}
		} while(!loggedOut);
	}
}
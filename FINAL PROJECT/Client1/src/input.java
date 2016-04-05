/*
 * Input Class is a class the takes in user input,
 * Then formats all input into a desired format of SET,GET,DEL,LOG queries
 * Lastly it calls the client class to process the request 
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class input{
	public static void main(String[] args) throws IOException{
		Scanner userInput = new Scanner(System.in);
		String input = ""; 
		boolean loggedOut = false;
		//	while(!loggedOut){

		String note="";
		int option = 0;
		/*
		 * Listing user options
		 */

		System.out.println("What would you like to do?");;
		System.out.println("1: to search by a professors name.");
		System.out.println("2: to add a new professor");
		System.out.println("3: to remove an existing professor");
		System.out.println("4: to add a rating for an existing professor");
		System.out.println("Logout to stop the program.");			
		input = userInput.nextLine();

		if (input.equals("1")) {
			/*
			 * Search by a professor
			 */
			note = "GET;";

			System.out.println("Enter the first name of the professor");
			note = note + userInput.nextLine() + ";";
			System.out.println("Enter the last name of the professor");
			note = note + userInput.nextLine();
			//queries.viewRatings(connection.SQLConnection(), "ratemyprof", myInput);

		} else if (input.equals("2")) {
			/*
			 * Add a new professor
			 */
			note = "SET;";

			System.out.println("Enter the professors first name");
			note = note + userInput.nextLine() + ";";
			System.out.println("Enter the professors last name");
			note = note + userInput.nextLine() + ";";
			System.out.println("Enter the professors school");
			note = note + userInput.nextLine();

			//	queries.addProf(connection.SQLConnection(), "ratemyprof", newFName, newLName, newSchool);


		} else if (input.equals("3")) {
			/*
			 * Delete a rating from the database
			 */
			note = "DEL;";

			System.out.println("Enter the professors first name");
			note = note + userInput.nextLine() + ";";
			System.out.println("Enter the professors last name");
			note = note + userInput.nextLine();
			//	queries.removeProf(connection.SQLConnection(), "ratemyprof", oldFName, oldLName);


		} else if (input.equals("4")) {
			/*
			 * Add a rating for a professor
			 */
			note = "SET;";

			System.out.println("Enter the professors first name");
			note = note + userInput.nextLine() + ";";
			System.out.println("Enter the professors last name");
			note = note + userInput.nextLine() + ";";
			System.out.println("Enter any comments you have for the professor");
			note = note + userInput.nextLine() + ";";
			System.out.println("Enter the 4 letter class abbrevation for the class the professor taught");
			note = note + userInput.nextLine() + ";";
			System.out.println("Enter the class number for the class the professors taught");
			note = note + userInput.nextInt() + ";";
			System.out.println("Enter the professors rating");
			note = note + userInput.nextInt();
			userInput.nextLine();

			//queries.addRating(connection.SQLConnection(), "ratemyprof", oldFName, oldLName, rating, className, classNumber, comments);

		} else if (input.equals("Logout")) {
			loggedOut = true;
		}else{
			System.out.println("User INPUT is invalid, please try again");
			System.exit(1);
		}


		userInput.reset();			
		client aClient = new client();
		note.trim();
		aClient.running(note);

		//	}
	}
}
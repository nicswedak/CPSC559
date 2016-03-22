/*
 * Class used to run the server
 * All values are pre-defined but can be modified to alter
 * 	the port
 *  and the server up time
 */


public class runServer{
	public static void main(String[] args){
		//Creating a server and indicating what the local host will be when the server runs
		Server server = new Server(9000);
	
		//Starting the server
		new Thread(server).start();
        System.out.println("Server Started and listening to the port 9000 \n");
		try {
			//Length of time the server is active
			Thread.sleep(100 * 5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Turning the server off \n");
		//AFter the time has run out the server turns off
		server.stop();
		return;
	}
}
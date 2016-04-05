
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

public class Server implements Runnable{
	protected int          serverPort   = 9090;
	protected ServerSocket serverSocket = null;
	protected boolean      isStopped    = false;
	protected Thread       runningThread= null;

	public Server(int port){
		this.serverPort = port;
	}

	public int threadCount;

	/*
	 * Function that creates a thread for the server and procceses error handling
	 */
	public void run(){
		synchronized(this){
			this.runningThread = Thread.currentThread();
		}
		openSocket();

		/*
		 * Continue to operate and keep the server alive until it dies
		 */
		while(! isStopped()){
			Socket clientSocket = null;
			try {
				clientSocket = this.serverSocket.accept();
				new Thread(new processReq(clientSocket, "OUR TEST SERVER")).start();
			} catch (IOException e) {
				if(isStopped()) {
					System.out.println("Server has been disconnected.") ;
					return;
				}
				throw new RuntimeException("Issues connecting to the server", e);
			}

		}
		System.out.println("Server Stopped.") ;
	}
/*
 * Check if the server is still alive or not
 */
	private synchronized boolean isStopped() {
		return this.isStopped;
	}

	/*
	 * Used to stop a server
	 */
	public synchronized void stop(){
		this.isStopped = true;
		try {
			this.serverSocket.close();
		} catch (IOException e) {
			throw new RuntimeException("Server Error - Disconnecting", e);
		}
	}

	/*
	 * Opening a socket that clients/proxy etc can connect to
	 */
	private void openSocket() {
		try {
			this.serverSocket = new ServerSocket(this.serverPort);
		} catch (IOException e) {
			throw new RuntimeException("Cannot open port: " + serverPort, e);
		}
	}
}
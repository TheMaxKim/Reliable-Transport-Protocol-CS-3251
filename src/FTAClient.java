import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Scanner;


public class FTAClient {
	
	public static void main (String[] args) throws Exception {
		
		
		if (args.length != 3) {
			System.out.println("Invalid number of arguments. Correct usage involves three command-line arguments, \"fta-client X A P\".");
			System.out.println("X is the port number the file transfer client should bind to, and should be equal to the server's port number minus one.");
			System.out.println("A is the IP address of NetEmu.");
			System.out.println("P is the UDP port number of NetEmu.");
			System.exit(0);
		}
		
		int hostPort = Integer.parseInt(args[0]);
   
		System.out.println("FTA Client started.");
		
		// Client port is always equal to server port - 1.
		int destinationPort = hostPort + 1;
   
   
		InetAddress IPAddress = InetAddress.getByName(args[1]);
		int UDPPortNumber = Integer.parseInt(args[2]);
		
		Scanner keyboard = new Scanner(System.in);

		
		
		
	}
	
}

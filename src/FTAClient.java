import java.util.Scanner;


public class FTAClient {
	
	public static void main (String[] args) {
		
		if (args.length != 3) {
			System.out.println("Invalid number of arguments. Correct usage involves three command-line arguments, \"fta-client X A P\".");
			System.out.println("X is the port number the file transfer client should bind to, and should be equal to the server's port number minus one.");
			System.out.println("A is the IP address of NetEmu.");
			System.out.println("P is the UDP port number of NetEmu.");
			System.exit(0);
		}
		
		int serverPort = Integer.parseInt(args[0]);
		int IPAddress = Integer.parseInt(args[1]);
		int UDPPortNumber = Integer.parseInt(args[2]);
		
		
		Scanner keyboard = new Scanner(System.in);
		
		
		
	}
	
}

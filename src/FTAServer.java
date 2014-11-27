import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 *    FTA Server
 */

public class FTAServer{
   
   public static void main(String args[]) throws Exception{
	   
	   if (args.length != 3) {
			System.out.println("Invalid number of arguments. Correct usage involves three command-line arguments, \"fta-server X A P\".");
			System.out.println("X is the port number the file transfer server should bind to, and should be equal to the client's port number + 1");
			System.out.println("A is the IP address of NetEmu.");
			System.out.println("P is the UDP port number of NetEmu.");
			System.exit(0);
	   }
	   
	   System.out.println("FTAServer started");

	   
	   final int hostPort = Integer.parseInt(args[0]);
	   
	   // Client port is always equal to server port - 1.
	   final int destinationPort = hostPort - 1;
	   
	   
	   final InetAddress IPAddress = InetAddress.getByName(args[1]);
	   final int UDPPortNumber = Integer.parseInt(args[2]);
	   
	   
	   /*
	    * Runs the server listen on a separate thread, so that the application isn't blocked
	    * and the user can still manage the FTA server while it is listening for connections and data.
	    */
	   Thread serverThread = new Thread() {
		   public void run() {
			   
			   RTP serverRTP = new RTP(IPAddress, hostPort, destinationPort);
			   try {
				   serverRTP.startServer();
				   serverRTP.listen();
			   } catch (IOException e) {
				   // TODO Auto-generated catch block
				   e.printStackTrace();
			   }
		   }
	   };
	  
      //int State = 0;
      /* State
       * 0= CLOSED
       * 1= LISTEN
       * 2= ESTABLISHED
       */
      /* Old Code
      String portStr=args[0];
      String emuAdd=args[1];
      String emuPortStr=args[2];
      
      int port=Integer.parseInt(portStr);
      InetAddress IP=InetAddress.getByName(emuAdd);
      int emuPort = Integer.parseInt(emuPortStr);
      
      DatagramSocket serverSoc = new DatagramSocket(port);
      
      byte[] receiveData = new byte[1024];
      byte[] sendData = null;
      
      State = 1;  //Passive open to LISTEN state
      System.out.println("Server is running and listening.");
            
      Scanner keyboard=new Scanner(System.in);
            
      while(true){
         if(keyboard.hasNextLine()){
            String inStr=keyboard.nextLine();
            if(inStr.equalsIgnoreCase("Terminate")){
               State= 0;
               System.exit(0);
            }
         }
         if(State==1){           //LISTEN
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length); 
            serverSoc.receive(receivePacket);
            byte[] packet = receivePacket.getData();
            //TODO check if packet is SYN
            //TODO then, send SYN+ack
            State=2; //after sending SYN+ACK    
         }else if(State==2){     //ESTABLISHED 
            //TODO send file to client
         }                  
      }
      */
   }
}
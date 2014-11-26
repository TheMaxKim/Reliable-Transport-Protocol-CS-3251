import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 *    FTA Server
 */

public class FTAServer{
   
   public static void main(String args[]) throws Exception{
   
      int State = 0;
      /* State
       * 0= CLOSED
       * 1= LISTEN
       * 2= ESTABLISHED
       */

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
   }
}
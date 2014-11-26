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

      State = 1;  //Passive open to LISTEN state
      System.out.println("Server is running and listening.");
      
      String portStr=args[0];
      String emuAdd=args[1];
      String emuPortStr=args[2];
      
      int port=Integer.parseInt(portStr);
      InetAddress IP=InetAddress.getByName(emuAdd);
      int emuPort = Integer.parseInt(emuPortStr);
      
      DatagramSocket serverSoc = DatagramSocket(port);
      byte[] receiveData = new byte[1024];
      //
            
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
            //receives SYN. 
            //sends SYN+ack
            State=2;    
         }else if(State==2){     //ESTABLISHED
            //!!
         }                  
      }
   }
}
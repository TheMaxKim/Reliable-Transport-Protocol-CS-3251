import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Date;
import java.lang.*;
import org.apache.commons.net.ntp.NTPUDPClient; 
import org.apache.commons.net.ntp.TimeInfo;


public class RTP {
	
	private final int MAXBUFFER = 255;
	
	private DatagramSocket socket;
	private InetAddress serverAddress;
	
	DatagramPacket recvPacket;
	DatagramPacket sendPacket;
	private int sourcePort;
	private int destinationPort;
    private int threshold;        //RTT timeout threshold
	
    
    
	public RTP(InetAddress serverAddress, int sourcePort, int destinationPort) {
		this.serverAddress = serverAddress;
		this.sourcePort = sourcePort;
		this.destinationPort = destinationPort;
		try {
			socket = new DatagramSocket(destinationPort);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void establishConnection(int sourcePort, int destinationPort) throws IOException {
		RTPPacket synPacket;
		RTPHeader synHeader = new RTPHeader(sourcePort, destinationPort, 0);
		synHeader.setSYN(true);
		
		int initialTimestamp = getNTPTimeStamp();
		
		synHeader.setTimestamp(initialTimestamp);
		synPacket = new RTPPacket(synHeader, null);
		synPacket.updateChecksum();
		
		byte[] synPacketBytes = synPacket.getPacketByteArray();
		
		boolean established = false;
		sendPacket = new DatagramPacket(synPacketBytes, synPacketBytes.length, serverAddress, destinationPort);

		
		while (compareTimestamp(initialTimestamp) && !established) {
			if (!compareTimestamp(initialTimestamp)) {
				socket.send(sendPacket);
			}
			
		}
		
		System.out.println("Attempting to establish connection.");
	}
	
	public void send(byte[] data) throws IOException {
		sendPacket = new DatagramPacket(data, data.length, serverAddress, destinationPort);
		System.out.println("send " + Arrays.toString(data));
		socket.send(sendPacket);

	}
	
	public void startServer() throws SocketException {

		recvPacket = new DatagramPacket(new byte[MAXBUFFER], MAXBUFFER);
		
		
	}
	
	public void listen() throws IOException {

			socket.receive(recvPacket);
			
			byte[] receivedData = new byte[recvPacket.getLength()];
			
			receivedData = Arrays.copyOfRange(recvPacket.getData(), 0, recvPacket.getLength());
			
			System.out.println("recv " + Arrays.toString(receivedData));
			
			
		
	}
	
	public void open() {
		
	}
	
	/*
	 * For the passed in packet, checks to see if the CRC checksum in the packet's header
	 * is valid by recalculating the checksum.
	 */
	public static boolean validateChecksum(RTPPacket packet) {
		
		// Obtains the checksum from the passed in packet.
		int packetChecksum = packet.getHeader().getChecksum();
		
		// Recalculates a checksum for the packet for comparison.
		int calculatedChecksum = packet.calculateChecksum();
		System.out.println(packetChecksum);
		System.out.println(calculatedChecksum);
		// Returns the result of comparing the two checksums.
		return (packetChecksum == calculatedChecksum);
	}
	
	/*
	 * Retrieves an NTP timestamp from the specified time server.
	 */
	public static int getNTPTimeStamp() throws IOException {

      String TIME_SERVER = "time-a.nist.gov";   
		NTPUDPClient timeClient = new NTPUDPClient();
		InetAddress inetAddress = InetAddress.getByName(TIME_SERVER);
		TimeInfo timeInfo = timeClient.getTime(inetAddress);
		long returnTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
		
		System.out.println(Long.toBinaryString(returnTime));
		Date date = new Date(returnTime);
		System.out.println(date.toString());
      return splitTimeStamp(returnTime);
	}
	
	/*
	 * Splits the passed in 64 bit NTP timestamp and returns a 32 bit RTP timestamp.
	 */
	public static int splitTimeStamp(long timestamp) {
		int RTPTimestamp;
		
		int firstHalf = (int) ((timestamp >> 32) & 0x0000FFFF);
		
		int secondHalf = (int) ((timestamp >> 16) & 0x0000FFFF);
		
		System.out.println("whole     " + Long.toBinaryString(timestamp));
        System.out.println("firstHalf " + Integer.toBinaryString(firstHalf));
        System.out.println("secondHalf " + Integer.toBinaryString(secondHalf));
        
        RTPTimestamp = (firstHalf << 16) | secondHalf;
        System.out.println("RTPTimeStampBinaryString: " + Integer.toBinaryString(RTPTimestamp));
        
		return RTPTimestamp;		
	}
   
   /*
    * Compares timestamps for estimating RRT
    * Returns TRUE if difference is within threshold
    */
	public boolean compareTimestamp(int timestamp) throws IOException{
      int RTPTimestamp = this.getNTPTimeStamp();
      
      if(Math.abs(RTPTimestamp-timestamp)<=this.threshold){
         return true;
      }else{
         return false;
      }   
   }
}

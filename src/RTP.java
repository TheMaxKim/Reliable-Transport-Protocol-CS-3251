import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import java.util.ArrayList;

public class RTP {
	
	private final int MAXBUFFER = 255;
	
	private DatagramSocket socket;
	private InetAddress serverAddress;
	

	DatagramPacket recvPacket;
	DatagramPacket sendPacket;
	private int sourcePort;
	private int destinationPort;
    private int threshold;        //RTT timeout threshold
    private int sequenceNumber;
    
    private ArrayList<RTPPacket> packetSendBuffer;
    private ArrayList<RTPPacket> packetReceiveBuffer;

    
    /* State
     * 0= CLOSED
     * 1= LISTEN
     * 2= ESTABLISHED
     */
    private int state = 0;
    
    
	public RTP(InetAddress serverAddress, int sourcePort, int destinationPort) {
		this.serverAddress = serverAddress;
		this.sourcePort = sourcePort;
		this.destinationPort = destinationPort;
		
		packetSendBuffer = new ArrayList<RTPPacket>();
		packetReceiveBuffer = new ArrayList<RTPPacket>();
		
		
		try {
			socket = new DatagramSocket(sourcePort);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void establishConnection(int sourcePort, int destinationPort) throws IOException {
		RTPPacket synPacket;
		RTPHeader synHeader = new RTPHeader(sourcePort, destinationPort, 0);
		synHeader.setSYN(true);
		synHeader.setBEG(true);
		synHeader.setFIN(true);
		
		int initialTimestamp = getNTPTimeStamp();
		
		synHeader.setTimestamp(initialTimestamp);
		synPacket = new RTPPacket(synHeader, null);
		synPacket.updateChecksum();

		//System.out.println("header bytes" + Arrays.toString(synPacket.getHeader().getHeaderByteArray()));
		byte[] synPacketBytes = synPacket.getPacketByteArray();
		System.out.println("first checksum " + synPacket.getHeader().getChecksum());
		sendPacket = new DatagramPacket(synPacketBytes, synPacketBytes.length, serverAddress, destinationPort);
		System.out.println("send" + Arrays.toString(synPacketBytes));
		
		recvPacket = new DatagramPacket(new byte[MAXBUFFER], MAXBUFFER);

		socket.send(sendPacket);
		sequenceNumber++;
		state = 1;
		while (state == 1) {
			listen();
			state = 2;
		}
				
			
		
		
		System.out.println("Attempting to establish connection.");
	}
	
	public void send(byte[] data) throws IOException {
		sendPacket = new DatagramPacket(data, data.length, serverAddress, destinationPort);
		System.out.println("send " + Arrays.toString(data));
		sequenceNumber++;
		socket.send(sendPacket);

	}
	
	public void startServer() throws SocketException {

		recvPacket = new DatagramPacket(new byte[MAXBUFFER], MAXBUFFER);
		state = 1;
	}
	
	public void listen() throws IOException {
		System.out.println("listen");
		
		while (state == 1) {
			recvPacket = new DatagramPacket(new byte[MAXBUFFER], MAXBUFFER);
			socket.receive(recvPacket);
			
			
			byte[] receivedData = new byte[recvPacket.getLength()];
			
			receivedData = Arrays.copyOfRange(recvPacket.getData(), 0, recvPacket.getLength());
			
			RTPPacket receivedRTPPacket = new RTPPacket(receivedData);
			RTPHeader receivedHeader = receivedRTPPacket.getHeader();
			
			//System.out.println("data length " + receivedData.length + "packet length " + receivedRTPPacket.getPacketByteArray().length);
			//System.out.println(receivedRTPPacket.getHeader().getChecksum());
			//System.out.println("rtppacket" + Arrays.toString(receivedRTPPacket.getPacketByteArray()));
			//System.out.println(receivedRTPPacket.calculateChecksum());
			
			
			if (receivedHeader.getChecksum() == receivedRTPPacket.calculateChecksum()) {
				
				
				packetReceiveBuffer.add(receivedRTPPacket);
				
				while (!receivedHeader.isFIN()) {
					recvPacket = new DatagramPacket(new byte[MAXBUFFER], MAXBUFFER);
					socket.receive(recvPacket);
					
					receivedRTPPacket = new RTPPacket(receivedData);
					receivedHeader = receivedRTPPacket.getHeader();
					if (receivedHeader.getChecksum() == receivedRTPPacket.calculateChecksum()) {
						packetReceiveBuffer.add(receivedRTPPacket);
					}
				}
			}
			
			
			if (state == 1) {
				
			}
			
						
			System.out.println("recv " + Arrays.toString(receivedData));

		}
			
		
	}
	
	/*
	 * Takes in a file name and converts it into a byte array, and then splits this byte array into
	 * packets to send.
	 */
	public void sendFile(String filename) {
		File file = new File(filename);
		
		byte[] byteArray = new byte[(int) file.length()];
		
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			fileInputStream.read(byteArray);
			
			byte[] packetBytes;
			
			if (byteArray.length < MAXBUFFER) {
				packetBytes = new byte[byteArray.length];
			} else {
				packetBytes = new byte[MAXBUFFER];
			}
			
			int packetNumber = 0;
			
			for (int i = 0; i < byteArray.length; i++) {
				packetBytes[i] = byteArray[(packetNumber * MAXBUFFER) + i];

				RTPHeader rtpHeader = new RTPHeader(sourcePort, destinationPort, packetNumber);
				if (packetNumber == 0) {
					rtpHeader.setBEG(true);
				} else if ((packetNumber * MAXBUFFER) == byteArray.length){
					rtpHeader.setFIN(false);
				}
				rtpHeader.setSequenceNumber(sequenceNumber++);
				rtpHeader.setWindowSizeOffset(packetNumber);
				rtpHeader.setTimestamp(getNTPTimeStamp());
								
				RTPPacket rtpPacket = new RTPPacket(rtpHeader, packetBytes);
				rtpPacket.updateChecksum();
				
				//Limit each packet to 254 bytes.
				if (i % 254 == 0) {
					sendPacket = new DatagramPacket(packetBytes, packetBytes.length, serverAddress, destinationPort);
					packetNumber += 255;
					socket.send(sendPacket);
				}
			}

			sendPacket = new DatagramPacket(packetBytes, packetBytes.length, serverAddress, destinationPort);
			socket.send(sendPacket);
			
			
			fileInputStream.close();
		} catch (FileNotFoundException e) {
			System.out.println("File was not found.");
				
		} catch (IOException e) {
			System.out.println("Error reading file.");
			
		}
		
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
		
		Date date = new Date(returnTime);
		return splitTimeStamp(returnTime);
	}
	
	/*
	 * Splits the passed in 64 bit NTP timestamp and returns a 32 bit RTP timestamp.
	 */
	public static int splitTimeStamp(long timestamp) {
		int RTPTimestamp;
		
		int firstHalf = (int) ((timestamp >> 32) & 0x0000FFFF);
		
		int secondHalf = (int) ((timestamp >> 16) & 0x0000FFFF);
		

        
        RTPTimestamp = (firstHalf << 16) | secondHalf;
        
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

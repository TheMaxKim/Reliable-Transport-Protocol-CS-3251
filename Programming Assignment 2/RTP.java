import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

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
    private int sequenceNumber;
    private String filename;
    
    private ArrayList<RTPPacket> packetSendBuffer;
    private ArrayList<RTPPacket> packetReceiveBuffer;

    private boolean isServer;
    
    /* State
     * 0= CLOSED
     * 1= LISTEN
     * 2= ESTABLISHED
     */
    private int state = 0;
    
    
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public RTP(InetAddress serverAddress, int sourcePort, int destinationPort, boolean isServer) {
		this.serverAddress = serverAddress;
		this.sourcePort = sourcePort;
		this.destinationPort = destinationPort;
		this.isServer = isServer;
		
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
		sendPacket = new DatagramPacket(synPacketBytes, synPacketBytes.length, serverAddress, destinationPort);

		
		recvPacket = new DatagramPacket(new byte[MAXBUFFER], MAXBUFFER);

		socket.send(sendPacket);
		sequenceNumber++;
		state = 1;
		
		listen();
		
	}
	
	public void sendRTPPacket(byte[] data) throws IOException {
		RTPPacket sendPacket;
		RTPHeader sendHeader = new RTPHeader(sourcePort, destinationPort, sequenceNumber);

		sendHeader.setBEG(true);
		sendHeader.setFIN(true);
		
		int initialTimestamp = getNTPTimeStamp();
		
		sendHeader.setTimestamp(initialTimestamp);
		sendPacket = new RTPPacket(sendHeader, data);
		sendPacket.updateChecksum();
		
		send(sendPacket.getPacketByteArray());
	}
	
	public void send(byte[] data) throws IOException {
		sendPacket = new DatagramPacket(data, data.length, serverAddress, destinationPort);
		sequenceNumber++;
		socket.send(sendPacket);

	}
	
	public void startServer() throws SocketException {

		recvPacket = new DatagramPacket(new byte[MAXBUFFER], MAXBUFFER);
		state = 1;
	}
	
	public void listen() throws IOException {
		
		
		while ((!isServer && state == 1) || (isServer && state != 0)) {
			recvPacket = new DatagramPacket(new byte[MAXBUFFER], MAXBUFFER);
			socket.receive(recvPacket);
			
			byte[] receivedData = new byte[recvPacket.getLength()];
			
			receivedData = Arrays.copyOfRange(recvPacket.getData(), 0, recvPacket.getLength());
			
			RTPPacket receivedRTPPacket = new RTPPacket(receivedData);
			RTPHeader receivedHeader = receivedRTPPacket.getHeader();
			
			//System.out.println("data length " + receivedData.length + "packet length " + receivedRTPPacket.getPacketByteArray().length);
			//System.out.println(receivedRTPPacket.getHeader().getChecksum());
			System.out.println("rtppacket" + Arrays.toString(receivedRTPPacket.getPacketByteArray()));
			//System.out.println(receivedRTPPacket.calculateChecksum());
			
			
			if (receivedHeader.getChecksum() == receivedRTPPacket.calculateChecksum()) {
				
				if (isServer && receivedHeader.isSYN()) {
					RTPHeader responseHeader = new RTPHeader(sourcePort, destinationPort, 0);
					RTPPacket responsePacket = new RTPPacket(responseHeader, null);
					
					responseHeader.setSequenceNumber(sequenceNumber);
					responseHeader.setTimestamp(getNTPTimeStamp());
					responseHeader.setACK(true);
					responseHeader.setSYN(true);
					responseHeader.setBEG(true);
					responseHeader.setFIN(true);
					
					responsePacket.updateChecksum();
					
					byte[] packetBytes = responsePacket.getPacketByteArray();
					sendPacket = new DatagramPacket(packetBytes, packetBytes.length, serverAddress, destinationPort);
					socket.send(sendPacket);
				}
				
				if (!isServer && receivedHeader.isSYN() && receivedHeader.isACK()) {
					
					state = 2;
				} else if (isServer && receivedHeader.isSYN()) {
					state = 2;
				}
								
				while (!receivedHeader.isFIN()) {
					recvPacket = new DatagramPacket(new byte[MAXBUFFER], MAXBUFFER);
					socket.receive(recvPacket);
					
					receivedRTPPacket = new RTPPacket(receivedData);
					receivedHeader = receivedRTPPacket.getHeader();
					if (receivedHeader.getChecksum() == receivedRTPPacket.calculateChecksum()) {
						packetReceiveBuffer.add(receivedRTPPacket);
					}
				}
				
				if (isServer && state == 2) {
					if (receivedRTPPacket.getData() != null) {
						sendFilePackets(new String(receivedRTPPacket.getData(), Charset.forName("UTF-8")));
					}
				}
				
				if (!isServer && state == 1) {
					packetReceiveBuffer.add(receivedRTPPacket);
				}
				
				if (!isServer && state == 1 && receivedHeader.isFIN()) {
					ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
					for (RTPPacket packet : packetReceiveBuffer) {
						outputStream.write(packet.getData());
					}
					createFileFromByteArray(filename, outputStream.toByteArray());
					packetReceiveBuffer.clear();
					state = 0;
				}
			}

		}
			
		
	}
	
	
	/*
	 * Takes in a file name and converts it into a byte array, and then splits this byte array into
	 * packets to send.
	 */
	public void sendFilePackets(String filename) {
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
			RTPHeader rtpHeader = new RTPHeader(sourcePort, destinationPort, packetNumber);
			RTPPacket rtpPacket;
			
			for (int i = 0; i < byteArray.length; i++) {
				packetBytes[i] = byteArray[(packetNumber * (MAXBUFFER-1)) + i];



								

				
				//Limit each packet to 254 bytes.
				if (i % 255 == 0 && !(i < 255)) {
					rtpHeader.setSequenceNumber(sequenceNumber++);
					rtpHeader.setWindowSizeOffset(packetNumber);
					rtpHeader.setTimestamp(getNTPTimeStamp());
					rtpPacket = new RTPPacket(rtpHeader, packetBytes);
					rtpPacket.updateChecksum();
					
					if (packetNumber == 0) {
						rtpHeader.setBEG(true);
					}

					packetNumber += 1;
					sendRTPPacket(rtpPacket.getPacketByteArray());
				}
			}
			
			rtpHeader.setFIN(true);
			rtpHeader.setSequenceNumber(sequenceNumber++);
			rtpHeader.setWindowSizeOffset(packetNumber);
			rtpHeader.setTimestamp(getNTPTimeStamp());
			rtpPacket = new RTPPacket(rtpHeader, packetBytes);
			rtpPacket.updateChecksum();
			System.out.println(rtpPacket.getPacketByteArray());
			sendRTPPacket(rtpPacket.getPacketByteArray());
			
			
			fileInputStream.close();
		} catch (FileNotFoundException e) {
			System.out.println("File was not found.");		
		} catch (IOException e) {
			System.out.println("Error reading file.");
			
		}
		state = 0;
	}
	
	
	public void createFileFromByteArray(String filename, byte[] fileByteArray) {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(filename);
			fileOutputStream.write(fileByteArray);
		} catch (FileNotFoundException e) {
			System.out.println("File was not found.");
		} catch (IOException e) {
			System.out.println("Error writing file.");
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

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
}

import java.io.IOException;
import java.net.DatagramSocket;

import java.net.InetAddress;
import java.util.Date;
import org.apache.commons.net.ntp.NTPUDPClient; 
import org.apache.commons.net.ntp.TimeInfo;


public class RTP {
	private DatagramSocket socket;
	private InetAddress serverAddress;
	private int sourcePort;
	private int destinationPort;
	
	public RTP(InetAddress serverAddress, int sourcePort, int destinationPort) {
	
	}
	
	public void open() {
		
	}
	
	/*
	 * For the passed in packet, checks to see if the CRC checksum in the packet's header
	 * is valid by recalculating the checksum.
	 */
	public boolean validateChecksum(RTPPacket packet) {
		
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
	public static void getNTPTimeStamp() throws IOException {
		String TIME_SERVER = "time-a.nist.gov";   
		NTPUDPClient timeClient = new NTPUDPClient();
		InetAddress inetAddress = InetAddress.getByName(TIME_SERVER);
		TimeInfo timeInfo = timeClient.getTime(inetAddress);
		long returnTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
		
		System.out.println(Long.toBinaryString(returnTime));
		Date date = new Date(returnTime);
		System.out.println(date.toString());
        splitTimeStamp(returnTime);
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
	
}

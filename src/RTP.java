import java.io.IOException;
import java.net.DatagramSocket;

import java.net.InetAddress;
import java.util.Date;
import org.apache.commons.net.ntp.NTPUDPClient; 
import org.apache.commons.net.ntp.TimeInfo;


public class RTP {
	private DatagramSocket socket;
	
	public static void getNTPTimeStamp() throws IOException {
		String TIME_SERVER = "time-a.nist.gov";   
		NTPUDPClient timeClient = new NTPUDPClient();
		InetAddress inetAddress = InetAddress.getByName(TIME_SERVER);
		TimeInfo timeInfo = timeClient.getTime(inetAddress);
		long returnTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
		

        splitTimeStamp(returnTime);
	}
	
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

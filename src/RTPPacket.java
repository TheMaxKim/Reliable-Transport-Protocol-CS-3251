import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.Checksum;


public class RTPPacket {

	// Header for the RTP packet
	private RTPHeader header;
	
	// Data to be delivered in the RTP packet
	private byte[] data;
	
	// Constructor for a RTP packet
	public RTPPacket(int sourcePort, int destinationPort, byte[] data) {
		this.setHeader(new RTPHeader(sourcePort, destinationPort, 0));
		this.setData(data);
	}
	
	public byte[] getPacketByteArray() {
		byte[] packetByteArray;
		byte[] headerByteArray;
		
		headerByteArray = header.getHeaderByteArray();
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			outputStream.write(headerByteArray);
			outputStream.write(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		packetByteArray = outputStream.toByteArray();
		
		return packetByteArray;
	}
	
	public RTPHeader getHeader() {
		return header;
	}

	public void setHeader(RTPHeader header) {
		this.header = header;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	/*
	 * Used to update the checksum field in an RTP packet header
	 */
	public void updateChecksum() {
		header.setChecksum(calculateChecksum());
	}
	
	/*
	 * Calculates the CRC checksum of the packet, assuming the checksum field is set to 0.
	 */
	public int calculateChecksum() {
		int checksumValue;
		
		Checksum checksum = new CRC32();
		
		byte[] packetByteArray = getPacketByteArray();
		
		// Calculate the checksum value assuming the checksum header has a value of 0.
		packetByteArray[16] = 0x00000000;

		checksum.update(packetByteArray, 0, packetByteArray.length);
		checksumValue = (int) checksum.getValue();
		return checksumValue;
	}

}

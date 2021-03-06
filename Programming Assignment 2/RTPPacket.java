import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.CRC32;
import java.util.zip.Checksum;


public class RTPPacket {

	// Header for the RTP packet
	private RTPHeader header;
	
	// Data to be delivered in the RTP packet
	private byte[] data;
	
	/*
	 * Constructor for a RTP packet with only the source port, destination port, and data
	 */
	public RTPPacket(int sourcePort, int destinationPort, byte[] data) {
		this.setHeader(new RTPHeader(sourcePort, destinationPort, 0));
		this.setData(data);
	}

	/*
	 * Constructor for a RTP packet with only the source port and destination port
	 */
	public RTPPacket(int sourcePort, int destinationPort) {
		this.setHeader(new RTPHeader(sourcePort, destinationPort, 0));
		this.setData(data);
	}

	
	/*
	 * Constructor for a RTP packet with a provided header and data
	 */
	public RTPPacket(RTPHeader header, byte[] data) {
		this.setHeader(header);
		this.setData(data);
	}
	
	/*
	 *  Constructor for a RTP packet from a byte array.
	 */
	public RTPPacket(byte[] packetByteArray) {
		// Obtains the bytes belonging to the header
		byte[] headerBytes = Arrays.copyOfRange(packetByteArray, 0, 28);
		this.setHeader(new RTPHeader(headerBytes));

		//System.out.println("header bytes\n" + Arrays.toString(headerBytes));
		
		// Obtains the rest of the bytes that are the packet data.
		if (packetByteArray.length > 28) {
			byte[] dataBytes = Arrays.copyOfRange(packetByteArray, 28, packetByteArray.length);
			this.setData(dataBytes);
		}
		//System.out.println("data bytes\n" + Arrays.toString(dataBytes));
		

		
	}
	
	public byte[] getPacketByteArray() {
		byte[] packetByteArray;
		byte[] headerByteArray;
		
		headerByteArray = header.getHeaderByteArray();
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			outputStream.write(headerByteArray);
			if (data != null) {
				outputStream.write(data);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		packetByteArray = outputStream.toByteArray();
		
		//System.out.println("packetByteArray\n" + Arrays.toString(packetByteArray));
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
		
		CRC32 checksum = new CRC32();
		
		byte[] packetByteArray = getPacketByteArray();
		
		// Calculate the checksum value assuming the checksum header has a value of 0.
		packetByteArray[16] = 0x00;
		packetByteArray[17] = 0x00;
		packetByteArray[18] = 0x00;
		packetByteArray[19] = 0x00;
		//System.out.println(Arrays.toString(packetByteArray));

		checksum.update(packetByteArray);
		checksumValue = (int) checksum.getValue();
		//System.out.println("checksumValue" + checksumValue);
		return checksumValue;
	}

}


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



}

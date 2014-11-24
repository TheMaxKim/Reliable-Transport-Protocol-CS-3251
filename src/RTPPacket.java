
public class RTPPacket {

	// Header for the RTP packet
	private RTPHeader header;
	
	// Data to be delivered in the RTP packet
	private byte[] data;
	

	
	// Constructor for a RTP packet
	public RTPPacket(int sourcePort, int destinationPort, byte[] data) {

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


	private class RTPHeader {
		private int sourcePort;
		private int destinationPort;
		private int sequenceNumber;
		private int windowSizeOffset;
		private int checksum;
		
		private boolean ACK;
		private boolean NACK;
		private boolean SYN;
		private boolean FIN;
		private boolean BEG;
		
		private int timestamp;
		
		public RTPHeader() {
			super();
		}
		
		
	}
}

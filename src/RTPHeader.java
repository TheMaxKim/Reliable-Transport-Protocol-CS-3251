	public class RTPHeader {
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
		
		public RTPHeader(int sourcePort, int destinationPort, int sequenceNumber) {
			this.sourcePort = sourcePort;
			this.destinationPort = destinationPort;
			this.sequenceNumber = sequenceNumber;
		}
		
		
	}

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Arrays;

	public class RTPHeader {
		private int sourcePort;
		private int destinationPort;
		private int sequenceNumber;
		private int windowSizeOffset;
		private int checksum;
		
		public int getSourcePort() {
			return sourcePort;
		}

		public void setSourcePort(int sourcePort) {
			this.sourcePort = sourcePort;
		}

		public int getDestinationPort() {
			return destinationPort;
		}

		public void setDestinationPort(int destinationPort) {
			this.destinationPort = destinationPort;
		}

		private boolean ACK;
		private boolean NACK;
		private boolean SYN;
		private boolean FIN;
		private boolean BEG;
		
		private int timestamp;
		
		public RTPHeader() {
			this.windowSizeOffset = 0;
			this.checksum = 0;
			this.ACK = false;
			this.NACK = false;
			this.SYN = false;
			this.FIN = false;
			this.BEG = false;
			this.timestamp = 0;
		}
		
		public RTPHeader(int sourcePort, int destinationPort, int sequenceNumber) {
			
			this();
			
			this.sourcePort = sourcePort;
			this.destinationPort = destinationPort;
			this.sequenceNumber = sequenceNumber;
			

		}
		
		public void setSequenceNumber(int sequenceNumber) {
			this.sequenceNumber = sequenceNumber;
		}
		
		public int getSequenceNumber() {
			return sequenceNumber;
		}
		
		public void setWindowSizeOffset(int windowSizeOffset) {
			this.windowSizeOffset = windowSizeOffset;
		}
		
		public int getWindowSizeOffset() {
			return windowSizeOffset;
		}
		
		public boolean isACK() {
			return ACK;
		}

		public void setACK(boolean ACK) {
			this.ACK = ACK;
		}

		public boolean isNACK() {
			return NACK;
		}

		public void setNACK(boolean NACK) {
			this.NACK = NACK;
		}

		public boolean isSYN() {
			return SYN;
		}

		public void setSYN(boolean syn) {
			this.SYN = SYN;
		}

		public boolean isFIN() {
			return FIN;
		}

		public void setFIN(boolean FIN) {
			this.FIN = FIN;
		}

		public boolean isBEG() {
			return BEG;
		}

		public void setBEG(boolean BEG) {
			this.BEG = BEG;
		}

		public void setChecksum(int checksum) {
			this.checksum = checksum;
		}
		
		public int getChecksum() {
			return checksum;
		}
		
		public byte[] getHeaderByteArray() {
			//Initializes the byte array to return
			byte[] headerByteArray;
			
			//Allocates enough space for 7 32-bit words in a bytebuffer to return the fields in
			ByteBuffer byteBuffer = ByteBuffer.allocate(Integer.BYTES * 7);
			byteBuffer.order(ByteOrder.BIG_ENDIAN);
			
			//Places the following fields in the bytebuffer in the order they appear in the header.
			byteBuffer.putInt(sourcePort);
			byteBuffer.putInt(destinationPort);
			byteBuffer.putInt(sequenceNumber);
			byteBuffer.putInt(windowSizeOffset);
			byteBuffer.putInt(checksum);
			
			//Converts the flags to ints and then utilizes bitshifting and masking to create a binary string for the flag field row in the header.
			int ackByte = (ACK ? 1 : 0) << 31;
			int nackByte = (NACK ? 1 : 0) << 30;
			int synByte = (SYN ? 1 : 0) << 29;
			int finByte = (FIN ? 1 : 0) << 28;
			int begByte = (BEG ? 1 : 0) << 27;
			

			int flagsCombined = ackByte | nackByte | synByte | finByte | begByte;
			
			//System.out.println("flagsCombined " + flagsCombined);
			
		
			//Places the finished flag byte array in the byte buffer.
			byteBuffer.putInt(flagsCombined);
			
			//Places the last header, the timestamp in the byte buffer.
			byteBuffer.putInt(timestamp);
			//Converts the byte buffer into a byte array.
			headerByteArray = byteBuffer.array();
			
			//Returns the completed byte buffer.
			System.out.println(Arrays.toString(headerByteArray));
			return headerByteArray;
		}
		
		/*
		 * Constructor for an RTP Header from a passed in byte array.
		 */
		public RTPHeader(byte[] headerByteArray) {
			ByteBuffer byteBuffer = ByteBuffer.wrap(headerByteArray);
			IntBuffer intBuffer = byteBuffer.asIntBuffer();
			this.sourcePort = intBuffer.get(0);
			this.destinationPort = intBuffer.get(1);
			this.sequenceNumber = intBuffer.get(2);
			this.windowSizeOffset = intBuffer.get(3);
			this.checksum = intBuffer.get(4);
			this.timestamp = intBuffer.get(6);
			

			
			int flagsCombined = intBuffer.get(5);
			
			int ackInt = flagsCombined >>> 31;
			int nackInt = (flagsCombined >>> 30) & 0x1;
			int synInt = (flagsCombined >>> 29) & 0x1;
			int finInt = (flagsCombined >>> 28) & 0x1;
			int begInt = (flagsCombined >>> 27) & 0x1;
			
			/* For Testing
			System.out.println("sourcePort" + this.sourcePort);
			System.out.println("destinationPort" + this.destinationPort);
			System.out.println("windowSizeOffset" + this.windowSizeOffset);
			System.out.println("checksum" + this.checksum);
			System.out.println("timestamp" + this.timestamp);
			System.out.println("ackInt" + ackInt);
			System.out.println("nackInt" + nackInt);
			System.out.println("synInt" + synInt);
			System.out.println("finInt" + finInt);
			System.out.println("begInt" + begInt);
			*/
		}
		
		
		public int getTimestamp() {
			return timestamp;
		}
		
		public void setTimestamp(int timestamp) {
			this.timestamp = timestamp;
		}
	}

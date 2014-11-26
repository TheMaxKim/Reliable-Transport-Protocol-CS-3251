import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

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
		
		public RTPHeader() {
			this.windowSizeOffset = 0;
			this.checksum = 0;
			this.ACK = true;
			this.NACK = false;
			this.SYN = true;
			this.FIN = false;
			this.BEG = true;
			this.timestamp = 0;
		}
		
		public RTPHeader(int sourcePort, int destinationPort, int sequenceNumber) {
			
			this();
			
			this.sourcePort = sourcePort;
			this.destinationPort = destinationPort;
			this.sequenceNumber = sequenceNumber;
			

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
			
			//Converts the flags to corresponding bytes to be placed in the byte buffer
			int ackByte = (ACK ? 1 : 0) << 31;
			int nackByte = (NACK ? 1 : 0) << 30;
			int synByte = (SYN ? 1 : 0) << 29;
			int finByte = (FIN ? 1 : 0) << 28;
			int begByte = (BEG ? 1 : 0) << 27;
			

			int flagsCombined = ackByte | nackByte | synByte | finByte | begByte;
			System.out.println("flagsCombined " + flagsCombined);
			
		
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
		
		public void updateTimestamp() {
			
		}
		
		public int getTimestamp() {
			return timestamp;
		}
		
		public void setTimestamp(int timestamp) {
			this.timestamp = timestamp;
		}
	}

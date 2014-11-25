import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

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
			this.ACK = false;
			this.NACK = false;
			this.SYN = false;
			this.FIN = false;
			this.BEG = false;
			this.timestamp = 20;
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
			int ackByte = (byte) (ACK ? 1 : 0);
			int nackByte = (byte) (NACK ? 1 : 0);
			int synByte = (byte) (SYN ? 1 : 0);
			int finByte = (byte) (FIN ? 1 : 0);
			int begByte = (byte) (BEG ? 1 : 0);
			
			StringBuilder sb = new StringBuilder(5);
			sb.append(ackByte);
			sb.append(nackByte);
			sb.append(synByte);
			sb.append(finByte);
			sb.append(begByte);
			
			//Uses a byte array output stream to concatenate the flags
			ByteArrayOutputStream flagOutputStream = new ByteArrayOutputStream();
			
			flagOutputStream.write(ackByte);
			flagOutputStream.write(nackByte);
			flagOutputStream.write(synByte);
			flagOutputStream.write(finByte);
			flagOutputStream.write(begByte);
			
			//Fills the rest of the byte array output stream with 0's, to fill in for the optional space of the row in the header.
			for (int i = 0; i < 26; i++) {
				flagOutputStream.write(0);
			}
			
			//Places the finished flag byte array in the byte buffer.
			byteBuffer.put(flagOutputStream.toByteArray());
			
			//Places the last header, the timestamp in the byte buffer.
			byteBuffer.putInt(timestamp);
			
			//Converts the byte buffer into a byte array.
			headerByteArray = byteBuffer.array();
			
			//Returns the completed byte buffer.
			System.out.println(byteBuffer.toString());
			return headerByteArray;
		}
	}

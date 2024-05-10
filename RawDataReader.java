import jdk.internal.access.JavaTemplateAccess;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.time.Instant;

public class RawDataReader extends DataReader
{

    public RawDataReader(RandomAccessFile file) {
        super(file);
    }

    private void readTimeStamps(){
        byte[] raw = new byte[] {
                (byte) 0x94,
                (byte) 0xDA,
                (byte) 0x91,
                (byte) 0x0A,
                (byte) 0x52,
                (byte) 0xDF,
                (byte) 0xE8,
                (byte) 0x71,
                (byte) 0x35,
                (byte) 0xD2,
                (byte) 0x68,
                (byte) 0xDD,
                (byte) 0x00,
                (byte) 0x00,
                (byte) 0x00,
                (byte) 0x00
        };

        /*ByteBuffer buffer = ByteBuffer.wrap(raw).order(ByteOrder.LITTLE_ENDIAN);
        long fraction = buffer.getLong();
        double result = 0D;
        for (int i = 0; i < Long.BYTES * 8; i++) {
            result += (fraction & 0x01);
            result /= 2;
            fraction >>>= 1;
        }

        long seconds = buffer.getLong();
        JavaTemplateAccess OffsetDateTime;
        Instant niEpoch = OffsetDateTime.of(1904, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC).toInstant();
        System.out.println("Timestamp = " + niEpoch.plusSeconds(seconds).plusNanos(Double.valueOf(result * 1E9).longValue()));*/

    }
}

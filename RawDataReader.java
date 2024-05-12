import javax.xml.crypto.Data;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;

public class RawDataReader extends DataReader {
int currentOffsetRD;
boolean isFirstCall = true;
ArrayList<Object> rawData;
    public RawDataReader(RandomAccessFile file) {
        super(file);
    }

    public ArrayList<Object> getRawData(int dataTypeOfRawData, long numberOfRawDataValues, int currentOffset) throws IOException {
        //System.out.println("Data Type of Raw Data: " + dataTypeOfRawData);
        if(dataTypeOfRawData == 68) {
            rawData= readTimeStamps(numberOfRawDataValues, currentOffset);
            return rawData;
        }
        if(dataTypeOfRawData == 10){
           rawData = readDoubleFloat(numberOfRawDataValues, currentOffset);
           return rawData;
        }
        return null;
    }

    private ArrayList<Object> readTimeStamps(long numberOFRawData, int currentOffset) throws IOException {
        if (isFirstCall){
            currentOffsetRD = currentOffset;
            isFirstCall = false;
        }
        ArrayList<Object> timestamps = new ArrayList<>();
        int countOfTimeStamps = 0;
        for (int i = 0; i < numberOFRawData; i++) {
            byte[] raw = new byte[16];
            raw = readBytes(currentOffsetRD, 16);
            ByteBuffer buffer = ByteBuffer.wrap(raw).order(ByteOrder.LITTLE_ENDIAN);

            long fraction = buffer.getLong();
            double result = 0D;
            for (int j = 0; j < Long.BYTES * 8; j++) {
                result += (fraction & 0x01);
                result /= 2;
                fraction >>>= 1;
            }
            currentOffsetRD+=16;

            long seconds = buffer.getLong();
            Instant niEpoch = OffsetDateTime.of(1904, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC).toInstant();
            Object obejctNiEPoch =  niEpoch.plusSeconds(seconds).plusNanos(Double.valueOf(result * 1E9).longValue());

            countOfTimeStamps++;
            timestamps.add(obejctNiEPoch);
        }
        /*System.out.println(timestamps);
        System.out.println(countOfTimeStamps);
        System.out.println(currentOffsetRD);*/
        return timestamps;
    }
    private ArrayList<Object> readDoubleFloat(long numberOFRawData, int currentOffset) throws IOException {

        ArrayList<Object> doubleFloats = new ArrayList<>();
        doubleFloats.add("Raw datas are double floats");
        //TODO

        byte[] raw = new byte[8];
        raw = readBytes(currentOffset, 8);
        currentOffsetRD += 20600;
        return doubleFloats;


    }

}

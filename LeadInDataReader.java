import java.io.IOException;
import java.io.RandomAccessFile;

public class LeadInDataReader extends DataReader {
    private long currentSegmentOffset;
    private final String expectedTag = "TDSm";
    private final int tagOffset = 0;
    private final int maskOffset = 4;
    private final int versionOffset = 8;
    private final int nextSegmentOffset = 12;
    private static final int RawDataOffset = 20;


    public LeadInDataReader(RandomAccessFile file, long segmentOffset){
        super(file); //calls DataReader constructor
        this.currentSegmentOffset = segmentOffset;

    }



    public boolean isValidTag() throws IOException {
        return getTag().equals(expectedTag);
    }
    public String getTag() throws IOException{
        return readString(tagOffset, 4);
    }

    public long getNextSegment() throws IOException {
        return readInt64(nextSegmentOffset);
    }
    public int getVersion() throws IOException {
        return readInt32(versionOffset);
    }
    public int getMask() throws IOException {

        return readInt32(maskOffset);
    }

    public long getRawData() throws IOException {
        return readInt64(RawDataOffset);
    }
    public LeadInData createLeadInData() throws IOException {
        return new LeadInData(getTag(), getMask(), getVersion(), getNextSegment(), getRawData());
    }
}

public class LeadInData{

    private String tag;
    private int mask;
    private int version;
    private long segmentOffset;
    public static long rawDataOffset = 1656;

    public LeadInData(String tag, int mask, int version, long segmentOffset, long rawDataOffset){

        this.tag = tag;
        this.mask = mask;
        this.version = version;
        this.segmentOffset = segmentOffset;
        this.rawDataOffset = rawDataOffset;
    }

    public String getTag() { return tag; }
    public int getMask() { return mask; }
    public int getVersion() { return version; }
    public long getSegmentOffset() { return segmentOffset; }
    public long getRawDataOffset() { return rawDataOffset; }


}

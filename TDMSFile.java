import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class TDMSFile {
    private static RandomAccessFile file;
    private static ArrayList<TDMSSegment> segments = new ArrayList<TDMSSegment>();
    private static ArrayList<TDMSGroup> groups = new ArrayList<TDMSGroup>();

    private static ArrayList<TDMSProperty> tdmsFileInfo = new ArrayList<>();

    private TDMSFile(RandomAccessFile file) throws FileNotFoundException {
        this.file = file;
    }

    public static TDMSFile read(String filePath) throws IOException {

        try {
            int segmentOffset = 0;
            file = new RandomAccessFile(filePath, "r");
            long fileSize = file.length();
            System.out.println();
            TDMSFile tdmsFile = new TDMSFile(file);
            tdmsFile.readSegments(segmentOffset, fileSize);

        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + filePath);
            e.printStackTrace();
        }
        finally {
            if (file != null) {
                try {
                    file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return new TDMSFile(file);
    }

    private void readSegments(long segmentOffset, long fileSize) throws IOException {
        int leadInDataSizeInBytes = 28;
        /*System.out.println("Segment offset: " + segmentOffset);
        System.out.println("file size: " + fileSize);
        System.out.println("Segment offset  + leadIndata: " + (segmentOffset + leadInDataSizeInBytes));*/
        if (segmentOffset + leadInDataSizeInBytes < fileSize) {
            TDMSSegment segment = readSegment(segmentOffset);
            segmentOffset += segment.getLeadInData().getSegmentOffset();
            if (segment != null) {
                segments.add(segment);
                readSegments(segmentOffset, fileSize);
            }
        }
    }

    private TDMSSegment readSegment(long segmentOffset) throws IOException {
        LeadInDataReader leadInDataReader = new LeadInDataReader(file, segmentOffset);
        int leadInDataByteCount = 28;
        MetaDataReader metaDataReader = new MetaDataReader(file, segmentOffset + leadInDataByteCount);
        //leadInDataReader.readBytes(40, 248000);

        if (leadInDataReader.isValidTag()) {
            LeadInData leadInData = leadInDataReader.createLeadInData();
            MetaData metaData = metaDataReader.createMetaData();
            return new TDMSSegment(leadInData, metaData);

        }
        else {
            System.out.println("Invalid TDMS file tag. The file might not be a valid TDMS file.");
            return null;
        }
    }
    public ArrayList<TDMSProperty> getProperties(){

        for (TDMSSegment segment : segments){ //var
            MetaData metaData = segment.getMetaData();
            tdmsFileInfo = metaData.getTDMSFileProperties();
        }
        return tdmsFileInfo;
    }
    public TDMSGroup getGroup(String name){

        for (TDMSSegment segment : segments){ //var
            for (int i = 0; i < segments.size(); i++){
                for(int j = 0; j < groups.size(); j++){
                    TDMSGroup group = groups.get(j);
                    if ( group.getName().equals(name)){
                        return group;
                    }
                }
            }


        }
        return null;


    }
    public ArrayList<TDMSGroup> getGroups(){
        for (TDMSSegment segment : segments){
            MetaData metaData = segment.getMetaData();
            groups = metaData.getGroups();
        }
        return groups;
    }

    public void printMetaData() {
        for (TDMSSegment segment : segments) {
            MetaData metaData = segment.getMetaData();
            System.out.println("Segment Meta Data:");
            System.out.println("Number of objects: " + metaData.getNumberOfObjects());
        }
    }

    public void printLeadInData() {
        for (TDMSSegment segment : segments) {
            LeadInData leadInData = segment.getLeadInData();
            System.out.println("Segment LeadIn Data:");
            System.out.println("Tag: " + leadInData.getTag());
            System.out.println("Version Number: " + leadInData.getVersion());
            System.out.println("Mask: " + leadInData.getMask());
            System.out.println("Next Segment Offset: " + leadInData.getSegmentOffset());
            System.out.println("Raw Data Offset: " + leadInData.getRawDataOffset());
            System.out.println("--------------------------------");
        }
    }
}

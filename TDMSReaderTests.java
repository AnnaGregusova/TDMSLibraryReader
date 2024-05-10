import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

public class TDMSReaderTests {
    private RandomAccessFile raFile;
    private LeadInDataReader leadInDataReader;
     ExpectedLeadInData.LeadInDataExpectations leadInDataExpectations;
    String path1 = "/Users/annagregusova/java/TDMS_library/TDMS_files/Sample_2021_2496_20210916_123804.tdms";
    String path2 = "/Users/annagregusova/java/TDMS_library/TDMS_files/coilResistanceTest_20210819_150423_0001.tdms";
    String path3 = "/Users/annagregusova/java/TDMS_library/TDMS_files/Test_OK_Sample_20210916_125133 (2).tdms";
    String path4 = "/Users/annagregusova/java/TDMS_library/TDMS_files/Test_OK_Sample_20210916_125133 (1).tdms";
    String path5 = "/Users/annagregusova/java/TDMS_library/TDMS_files/sent_test_20200826_135215_0001.tdms";
    String path6 = "/Users/annagregusova/java/TDMS_library/TDMS_files/NH3_concentration_1a_0002.tdms";

    private void initializeReaderForFile(String filePath) throws IOException {
        this.raFile = new RandomAccessFile(filePath, "r");
        this.leadInDataReader = new LeadInDataReader(raFile, 0);

    }

    @BeforeEach
    void setUp() throws IOException {

        String filePath = path6;
        initializeReaderForFile(filePath);
        leadInDataExpectations = ExpectedLeadInData.getExpectations(filePath);
    }

    @Test
    @DisplayName("Test Tag Validity")
    void testTagValidity() throws IOException {
        assertTrue(leadInDataReader.isValidTag(), "The tag should be valid.");
    }

    @Test
    @DisplayName("Test Version Number")
    void testVersionNumber() throws IOException {

        assertEquals(leadInDataExpectations.version, leadInDataReader.getVersion(), "Version number should match expected.");
    }

    @Test
    @DisplayName("Test Mask Value")
    void testMaskValue() throws IOException {

        assertEquals(leadInDataExpectations.mask, leadInDataReader.getMask(), "Mask value should match expected.");
    }

    @Test
    @DisplayName("Test Next Segment Offset")
    void testNextSegmentOffset() throws IOException {

        assertEquals(leadInDataExpectations.nextSegmentOffset, leadInDataReader.getNextSegment(), "Next segment offset should match expected.");
    }

    @Test
    @DisplayName("Test Raw Data Offset")
    void testRawDataOffset() throws IOException {

        assertEquals(leadInDataExpectations.rawDataOffset, leadInDataReader.getRawData(), "Raw data offset should match expected.");
    }


}
class ExpectedLeadInData {
    static TDMSReaderTests tdmsTests = new TDMSReaderTests();

    static final Map<String, LeadInDataExpectations> expectationsMap = new HashMap<>();

    static {
        expectationsMap.put(tdmsTests.path1, new LeadInDataExpectations(true, 4713, 14, 258094, 1582));
        expectationsMap.put(tdmsTests.path2, new LeadInDataExpectations(true, 4713, 14, 124402, 1282));
        expectationsMap.put(tdmsTests.path3, new LeadInDataExpectations(true, 4713, 14, 248828, 1628));
        expectationsMap.put(tdmsTests.path4, new LeadInDataExpectations(true, 4713, 14, 248828, 1628));
        expectationsMap.put(tdmsTests.path5, new LeadInDataExpectations(true, 4713, 14, 534942, 2718));
        expectationsMap.put(tdmsTests.path6, new LeadInDataExpectations(true, 4713, 14, 2074193, 593));

    }

    public static LeadInDataExpectations getExpectations(String filePath) {
        return expectationsMap.getOrDefault(filePath, new LeadInDataExpectations(false, -1, -1, -1, -1));
    }

    public static class LeadInDataExpectations {
        boolean isValidTag;
        int version;
        int mask;
        long nextSegmentOffset;
        long rawDataOffset;

        public LeadInDataExpectations(boolean isValidTag, int version, int mask, long nextSegmentOffset, long rawDataOffset) {
            this.isValidTag = isValidTag;
            this.version = version;
            this.mask = mask;
            this.nextSegmentOffset = nextSegmentOffset;
            this.rawDataOffset = rawDataOffset;
        }
    }
}

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class MetaDataReader extends DataReader {
    private long metaDataOffset;
    private int groupNameOffset = 36;
    int numberOfObjects = 0;
    private boolean isFirstCallToGetGroups = true;
    private int currentOffset = 28;
    ArrayList<TDMSGroup> groups = new ArrayList<>();
    ArrayList<TDMSProperty> tdmsFileInfo = new ArrayList<>();
    private int numberOfPropertiesOffset = 0;
    private int chunkSize = 0;
    boolean FirstCall = true;
    int intRawDataIndex;



    public MetaDataReader(RandomAccessFile file, long metaDataOffset) throws IOException {

        super(file);
        this.metaDataOffset = metaDataOffset;

    }
    public boolean hasObjects() throws IOException {

        if (getNumberOfObjects() == 0){
            return false;
        }
        return true;
    }
    public String getName() throws IOException{

        return readString(groupNameOffset, getLengthOfObject()/2);
    }
    public int getNumberOfObjects() throws IOException {

        int numberOfObjects = readInt32(currentOffset); //zacinam na 28
        currentOffset += 4; //jsem na 32 a jde na delku
        return numberOfObjects;
    }

    public ArrayList<TDMSGroup> getGroups() throws IOException{

        ArrayList<TDMSChannel> channels = new ArrayList<>();
        ArrayList<TDMSProperty> properties = new ArrayList<>();

        if (isFirstCallToGetGroups) {
            numberOfObjects = getNumberOfObjects(); // This line runs only on the first call
            //System.out.println("Number of objects: " + numberOfObjects);
            isFirstCallToGetGroups = false; // Set flag to false after first call
        }

        numberOfObjects --;

        int lengthOfObjectPath = getLengthOfObject();
        String name = readString(currentOffset, lengthOfObjectPath);
        currentOffset += lengthOfObjectPath;
        //System.out.println("Group name: " + name);
        if (hasRawData() != -1){
            return null;
        }
        //System.out.println("Has Raw data " + hasRawData);

        properties = getProperties();

        processObjects(channels);

        if (name. equals("/")){

            TDMSGroup tdmsFileGroup = new TDMSGroup(name, properties, channels);
            tdmsFileInfo = tdmsFileGroup.getProperties();

        }
        else{
            groups.add(new TDMSGroup(name, properties, channels));
        }

        return groups;
    }
    private void processObjects(ArrayList<TDMSChannel> channels) throws IOException {

        if (numberOfObjects != 0) {
            if (isGroup(currentOffset)) {
                getGroups();
            } else {
                TDMSChannel tdmsChannel = getChannel();
                channels.add(tdmsChannel);
            }
            processObjects(channels); // Recursive call
        }
    }

    public MetaData createMetaData() throws IOException {
        return new MetaData(getGroups(), getTdmsFileInfo());//, getGroups(), getChannels());
    }

    private ArrayList<TDMSProperty> getTdmsFileInfo() {
        return tdmsFileInfo;
    }

    public int getLengthOfObject() throws IOException{
        int lengthOfObjectPath = readInt32(currentOffset); //jsem na 32
        currentOffset += 4; //jsem na 36 a jdu na jmeno
        return  lengthOfObjectPath;
    }
    public int getNumberOfProperties() throws IOException{

        int numberOfProperties =  readInt32(currentOffset); // jsem na 36 + delka jmena groupy
        currentOffset += 4;
        return numberOfProperties;

    }
    public int hasRawData() throws IOException{

        int hasRawData = readInt32(currentOffset);
        //readBytes(currentOffset, 8);
        currentOffset += 4;
        return hasRawData;

    }

    public ArrayList<TDMSProperty> getProperties() throws IOException {

        int numberOfProperties = getNumberOfProperties();
        ArrayList<TDMSProperty> properties = new ArrayList<TDMSProperty>();

        //System.out.println("Number of properties: " + numberOfProperties);

        for (int i = 0; i < numberOfProperties; i++){

            int lengthOfPropertyName = readInt32(currentOffset);
            currentOffset += 4;
            String propertyName = readString(currentOffset, lengthOfPropertyName);
            currentOffset += lengthOfPropertyName;
            PropertyDataTypeEnum propertyDataType = findDataTypeByValue(currentOffset);

            Object propertyValue;
            Object dataType;


            switch (propertyDataType) {
                case TDS_TYPE_I8:
                case TDS_TYPE_I16:
                case TDS_TYPE_I32:
                case TDS_TYPE_I64:
                case TDS_TYPE_U8:
                case TDS_TYPE_U16:
                case TDS_TYPE_U32:
                case TDS_TYPE_U64:
                case TDS_TYPE_SINGLE_FLOAT:
                case TDS_TYPE_EXTENDED_FLOAT:
                case TDS_TYPE_BOOLEAN:
                case TDS_TYPE_FIXED_POINT:
                case TDS_TYPE_DOUBLE_FLOAT:
                case TDS_TYPE_EXTENDED_FLOAT_WITH_UNIT:
                case TDS_TYPE_TIMESTAMP:
                    currentOffset += propertyDataType.getSize();
                    propertyValue = propertyDataType.name();
                    //System.out.println("Property value: " + propertyValue);
                    break;
                case TDS_TYPE_SINGLE_FLOAT_WITH_UNIT:
                    currentOffset += 12;
                    propertyValue = propertyDataType.name();
                    //System.out.println("Property value: " + propertyValue);
                    break;
                case TDS_TYPE_STRING:
                    int lengthOfPropertyValue = readInt32(currentOffset);
                    currentOffset += propertyDataType.getSize();
                    propertyValue = readString(currentOffset, lengthOfPropertyValue);
                    currentOffset += lengthOfPropertyValue;
                    //System.out.println("Property value: " + propertyValue);
                    break;
                default:
                    propertyValue = readInt32(numberOfPropertiesOffset + 16 + lengthOfPropertyName);
                    currentOffset += 4;
                    //System.out.println("Property value: " + propertyValue);
                    break;
            }
            //System.out.println("Current offset: " + currentOffset);

            properties.add(new TDMSProperty(propertyName, propertyValue, propertyDataType));
        }

        return properties;
    }

    private PropertyDataTypeEnum findDataTypeByValue(int offset) throws IOException {
        int propertyDataTypeValue = readInt32(offset);
        currentOffset += 4;
        for (PropertyDataTypeEnum type : PropertyDataTypeEnum.values()) {
            if (type.getValue() == propertyDataTypeValue) {
                return type;
            }
        }
        return PropertyDataTypeEnum.DS_TYPE_VOID;  //vyjimka
    }

    enum PropertyDataTypeEnum {

        DS_TYPE_VOID(0, 4),
        TDS_TYPE_I8(1, 0),
        TDS_TYPE_I16(2, 4),
        TDS_TYPE_I32(3, 4),
        TDS_TYPE_I64(4, 4),
        TDS_TYPE_U8(5, 4),
        TDS_TYPE_U16(6, 4),
        TDS_TYPE_U32(7, 4),
        TDS_TYPE_U64(8, 4),
        TDS_TYPE_SINGLE_FLOAT(9, 4),
        TDS_TYPE_DOUBLE_FLOAT(10, 8),
        TDS_TYPE_EXTENDED_FLOAT(11, 8),
        TDS_TYPE_SINGLE_FLOAT_WITH_UNIT(18, 9),    // 0x19 in decimal
        TDS_TYPE_DOUBLE_FLOAT_WITH_UNIT(26, 8),    // 0x1A in decimal
        TDS_TYPE_EXTENDED_FLOAT_WITH_UNIT(27, 8), // 0x1B in decimal
        TDS_TYPE_STRING(32, 4),                   // 0x20 in decimal
        TDS_TYPE_BOOLEAN(33, 4),                   // 0x21 in decimal
        TDS_TYPE_TIMESTAMP(68, 16),                // 0x44 in decimal
        TDS_TYPE_FIXED_POINT(79, 4),               // 0x4F in decimal
        TDS_TYPE_COMPLEX_SINGLE_FLOAT(0x08000C, 8),
        TDS_TYPE_COMPLEX_DOUBLE_FLOAT(0x10000D, 16),
        TDS_TYPE_DAQMX_RAW_DATA(0xFFFFFFFF, 4);

        private final int value;
        private final int size;

        PropertyDataTypeEnum(int value, int size) {
            this.value = value;
            this.size = size;
        }

        public int getValue() {
            return this.value;
        }

        public int getSize() {
            return this.size;
        }
    }
    private boolean isGroup(int currentOffset) throws IOException {
        int nextObjectPathLength = readInt32(currentOffset);
        String nextObjectName = readString(currentOffset + 4, nextObjectPathLength);
        int slashCount = nextObjectName.length() - nextObjectName.replace("/", "").length();
        return slashCount < 2; // If there are fewer than 2 slashes, it's a group
    }

    public TDMSChannel getChannel() throws IOException {

        numberOfObjects--;
        ArrayList<TDMSProperty> properties;
        ArrayList<Object> rawData;
        int nextObjectPathLength = readInt32(currentOffset);
        currentOffset += 4;
        String channelName = readString(currentOffset, nextObjectPathLength);
        currentOffset += nextObjectPathLength;

        int lengthOfIndexInformation = readInt32(currentOffset);

        currentOffset += 4;
        int dataTypeOfRawData = readInt32(currentOffset);
        byte [] raws = new byte[4];
        raws = readBytes(currentOffset,4);
        //System.out.println("Read data type: ");
        //printBytes(raws);
        currentOffset += 4;
        int dimension = readInt32(currentOffset);
        currentOffset += 4;
        //readBytes(currentOffset, 8);
        long numberOfRawDataValues = readInt64(currentOffset);
        ///System.out.println("NUMBER OF RAW DATA VALUES" + numberOfRawDataValues);
        currentOffset += 8;
        int rawDataSize = dataTypeOfRawData * (int) numberOfRawDataValues;


        int numberOfProperties = readInt32(currentOffset);
        /*System.out.println("CHANNEL NAME: " + channelName);
        System.out.println("lengthOfIndexInformation: " + lengthOfIndexInformation);
        System.out.println("dataTypeOfRawData: " + dataTypeOfRawData);
        System.out.println("dimension: " + dimension);
        System.out.println("numberOfProperties: " + numberOfProperties);
        System.out.println("Raw data Size: " + rawDataSize );
        //System.out.println("numberOfRawDataValues: " + numberOfRawDataValues);*/
        RawDataReader rawDataReader = new RawDataReader(this.file);
        if (numberOfProperties != 0) {

            properties = getProperties();
        }
        else {

            properties = null;
            currentOffset += 4;
        }


        if (FirstCall){
            long rawDataOffset = LeadInData.rawDataOffset + 28;
            intRawDataIndex = (int)rawDataOffset;
            //System.out.println("Raw data index: " + intRawDataIndex);
            rawData = rawDataReader.getRawData(dataTypeOfRawData, numberOfRawDataValues, intRawDataIndex);
            int size;
            if (dataTypeOfRawData == 68){ size = 16;}
            else {size = 8;}
            int intRawDataOffset = (int)rawDataOffset;
            long rawDataIndex = numberOfRawDataValues*size;
            intRawDataIndex += (int)rawDataIndex;
            FirstCall = false;
        }
        else{

            //System.out.println("Raw data index: " + intRawDataIndex);
            rawData = rawDataReader.getRawData(dataTypeOfRawData, numberOfRawDataValues, intRawDataIndex);
            int size;
            long rawDataOffset = LeadInData.rawDataOffset + 28;
            int intRawDataOffset = (int)rawDataOffset;
            if (dataTypeOfRawData == 68){ size = 16;}
            else{size = 8;}
            intRawDataIndex += numberOfRawDataValues*size;

        }
        /*byte[] raw = new byte[16];
        raw = readBytes(rawDataIndex, 16);
        printBytes(raw);*/


        TDMSChannel tdmsChannel = new TDMSChannel(channelName, properties, rawData);
        //System.out.println("Current offset: " + currentOffset );
        return tdmsChannel;
    }
    private int getRawDataIndex(long numberOfRawDataValues, int dataTypeOfRawData){

        if (FirstCall){
            long rawDataOffset = LeadInData.rawDataOffset + 28;
            intRawDataIndex = (int)rawDataOffset;
            FirstCall = false;
        }
        else{
            int size;
            long rawDataOffset = LeadInData.rawDataOffset + 28;
            int intRawDataOffset = (int)rawDataOffset;
            if (dataTypeOfRawData == 68){ size = 16;}
            else{size = 8;}
            long rawDataIndex = intRawDataOffset + numberOfRawDataValues*size;
            intRawDataIndex += (int)rawDataIndex;

        }
        return intRawDataIndex;
    }

}


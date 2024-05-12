import java.util.ArrayList;

public class MetaData{
    private int numberOfObjects;
    private ArrayList<TDMSGroup> groups;
    private ArrayList<TDMSProperty> TDMSFileProperties;
    // private List<TDMSChannel> channels;

    public int getNumberOfObjects() { return numberOfObjects; }

    public ArrayList<TDMSProperty> getTDMSFileProperties() {return TDMSFileProperties; }
    public ArrayList<TDMSGroup> getGroups() {return groups;}
    //public List<TDMSChannel> getChannels(){return channels;}
    public MetaData(ArrayList<TDMSGroup> groups, ArrayList<TDMSProperty> TDMSFileProperties){ // List<TDMSChannel> channels){
        this.groups = groups;
        this.TDMSFileProperties = TDMSFileProperties;
        //this.channels = channels;
    }
}





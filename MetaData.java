import java.util.ArrayList;
import java.util.List;

public class MetaData{
    private int numberOfObjects;
    private ArrayList<TDMSGroup> groups;
    private ArrayList<TDMSProperty> tdmsFileInfo;
   // private List<TDMSChannel> channels;

    public int getNumberOfObjects() { return numberOfObjects; }

    public ArrayList<TDMSProperty> getTdmsFileInfo() {return tdmsFileInfo; }
    public ArrayList<TDMSGroup> getGroups() {return groups;}
    //public List<TDMSChannel> getChannels(){return channels;}
    public MetaData(ArrayList<TDMSGroup> groups, ArrayList<TDMSProperty> tdmsFileInfo){ // List<TDMSChannel> channels){
        this.groups = groups;
        this.tdmsFileInfo = tdmsFileInfo;
        //this.channels = channels;
    }
}





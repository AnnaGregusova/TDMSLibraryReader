import java.util.ArrayList;

public class TDMSGroup {
    private ArrayList<TDMSProperty> properties;
    private String name;
    private ArrayList<TDMSChannel> channels;

    public TDMSGroup(String name, ArrayList<TDMSProperty> properties, ArrayList<TDMSChannel> channels){
        this.name = name;
        this.properties = properties;
        this.channels = channels;
    }

    public String getName() {return name;}
    public ArrayList<TDMSProperty> getProperties(){

        return properties;
    }
    public ArrayList<TDMSChannel> getChannels() {
        // Return the list of channels
        return this.channels;
    }

    public TDMSChannel getChannel(String name) {

        for (TDMSChannel channel : channels){ //var
            for (int i = 0; i < channels.size(); i++){
                if ( channel.getName().equals(name)){
                    System.out.println(channel.getName());
                    //System.out.println(channel.getProperties());
                    return channel;
                }
            }
        }
        return null;
    }
    @Override
    public String toString() {
        return "TDMSGroup { " +
                "name= " + name + ' ' +
                '}';
    }

}

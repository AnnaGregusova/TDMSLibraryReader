import java.util.ArrayList;

public class TDMSChannel{
    //private ArrayList<RawData> rawData;
    private String name;
    private ArrayList<TDMSProperty> properties;

    public TDMSChannel(String name, ArrayList<TDMSProperty> properties){//, ArrayList<RawData> rawData){
        this.name = name;
        this.properties = properties;
        //this.rawData = rawData;
    }

    public String getName() {return name;}
    public ArrayList<TDMSProperty> getProperties() {return properties;}

    public Object getPropertyValue(String name) {

        for (TDMSProperty property : properties){ //var
            for (int i = 0; i < properties.size(); i++){
                if ( property.getPropertyName().equals(name)){
                    Object propertyValue = property.getPropertyValue();
                    //System.out.println(channel.getProperties());
                    return propertyValue;
                }
            }
        }
        return null;
    }
    @Override
    public String toString() {
        return "TDMSChannel { " +
                "name= " + name + ' ' +
                '}';
    }
    /*public ArrayList<RawData> getRawData(){

        return rawData;
    }*/
}
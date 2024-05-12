public class TDMSProperty {

    private String propertyName;
    private Object propertyValue;
    private Object propertyDataType;
    public TDMSProperty(String name, Object propertyValue, Object propertyDataType){
        this.propertyName = name;
        this.propertyValue = propertyValue;
        this.propertyDataType = propertyDataType;
    }

    @Override
    public String toString() {
        return "Property { " +
                "name='" + propertyName + '\'' +
                ", value= " + propertyValue+
                '}';
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Object getPropertyValue() {
        return propertyValue;
    }
    public Object getPropertyDataType(){
        return propertyDataType;
    }
}


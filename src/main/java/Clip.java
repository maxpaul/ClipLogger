import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by advman on 2016-09-27.
 */
public class Clip {

    private final StringProperty timeStamp;
    private final StringProperty textValue;
    private final boolean btn;
    public BooleanProperty refreshBtn;


    /**
     * Default constructor.
     */
    public Clip() {
        this(null, null, false);
    }

    /**
     * Constructor with some initial data.
     *
     * @param timeStamp
     * @param textValue
     * @param btn
     */
    public Clip(String timeStamp, String textValue, boolean btn) {
        this.timeStamp = new SimpleStringProperty(timeStamp);
        this.textValue = new SimpleStringProperty(textValue);
        this.btn = false;
    }

    public String getTimeStamp() {
        return timeStamp.get();
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp.set(timeStamp);
    }

    public StringProperty timeStampProperty() {
        return timeStamp;
    }

    public BooleanProperty refreshBtnProperty() {
        return refreshBtn = new SimpleBooleanProperty();
    }

    public String getTextValue() {
        return textValue.get();
    }

    public void setTextValue(String textValue) {
        this.textValue.set(textValue);
    }

    public StringProperty textValueProperty() {
        return textValue;
    }

    public boolean btnProperty() {
        return btn;
    }
}
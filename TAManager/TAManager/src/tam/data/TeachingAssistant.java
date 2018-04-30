package tam.data;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * This class represents a Teaching Assistant for the table of TAs.
 * 
 * @author Richard McKenna
 */
public class TeachingAssistant<E extends Comparable<E>> implements Comparable<E>  {
    // THE TABLE WILL STORE TA NAMES AND EMAILS
    private final StringProperty name;
    private StringProperty email;

    /**
     * Constructor initializes the TA name
     */
    public TeachingAssistant(String initName) {
        name = new SimpleStringProperty(initName);
        email = new SimpleStringProperty("");
    }
    
    public TeachingAssistant(String initName, String initEmail) {
    		this(initName);
    		email = new SimpleStringProperty(initEmail);
    	
    }
    
    // COPY CONSTRUCTOR
    
    public TeachingAssistant (TeachingAssistant ta) {
    	name = new SimpleStringProperty(ta.getName());
    	email = new SimpleStringProperty(ta.getEmail());
    }

    // ACCESSORS AND MUTATORS FOR THE PROPERTIES

    public String getName() {
        return name.get();
    }

    public void setName(String initName) {
        name.set(initName);
    }
    
    public String getEmail() {
        return email.get();
    }

    public void setEmail(String initEmail) {
        email.set(initEmail);
    }
    

    @Override
    public int compareTo(E otherTA) {
        return getName().compareTo(((TeachingAssistant)otherTA).getName());
    }
    
    @Override
    public String toString() {
        return name.getValue() +" " + email.getValue();
    }
}
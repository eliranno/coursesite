package tam.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import djf.components.AppDataComponent;
import djf.ui.AppMessageDialogSingleton;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import properties_manager.InvalidXMLFileFormatException;
import properties_manager.PropertiesManager;
import tam.TAManagerApp;
import tam.TAManagerProp;
import tam.workspace.TAWorkspace;

/**
 * This is the data component for TAManagerApp. It has all the data needed
 * to be set by the user via the User Interface and file I/O can set and get
 * all the data from this object
 * 
 * @author Richard McKenna
 */
public class TAData implements AppDataComponent {

    // WE'LL NEED ACCESS TO THE APP TO NOTIFY THE GUI WHEN DATA CHANGES
    TAManagerApp app;

    // NOTE THAT THIS DATA STRUCTURE WILL DIRECTLY STORE THE
    // DATA IN THE ROWS OF THE TABLE VIEW
    ObservableList<TeachingAssistant> teachingAssistants;

    // THIS WILL STORE ALL THE OFFICE HOURS GRID DATA, WHICH YOU
    // SHOULD NOTE ARE StringProperty OBJECTS THAT ARE CONNECTED
    // TO UI LABELS, WHICH MEANS IF WE CHANGE VALUES IN THESE
    // PROPERTIES IT CHANGES WHAT APPEARS IN THOSE LABELS
    HashMap<String, StringProperty> officeHours;
    
    // THESE ARE THE LANGUAGE-DEPENDENT VALUES FOR
    // THE OFFICE HOURS GRID HEADERS. NOTE THAT WE
    // LOAD THESE ONCE AND THEN HANG ON TO THEM TO
    // INITIALIZE OUR OFFICE HOURS GRID
    ArrayList<String> gridHeaders;

    // THESE ARE THE TIME BOUNDS FOR THE OFFICE HOURS GRID. NOTE
    // THAT THESE VALUES CAN BE DIFFERENT FOR DIFFERENT FILES, BUT
    // THAT OUR APPLICATION USES THE DEFAULT TIME VALUES AND PROVIDES
    // NO MEANS FOR CHANGING THESE VALUES
    int startHour;
    int endHour;
    
    // DEFAULT VALUES FOR START AND END HOURS IN MILITARY HOURS
    public static final int MIN_START_HOUR = 9;
    public static final int MAX_END_HOUR = 20;
    public static final String XML_PROPERTIES_FILE_NAME = "app_properties.xml";
    public static final String XML_SCHEMA_FILE_NAME = "properties_schema.xsd";
    public static final String LOADING_XML_FILE_ERROR_MSG = "error loading xnl file";
    public static final String LOADING_XML_MSG_TITLE = "error";

    /**
     * This constructor will setup the required data structures for
     * use, but will have to wait on the office hours grid, since
     * it receives the StringProperty objects from the Workspace.
     * 
     * @param initApp The application this data manager belongs to. 
     */
    public TAData(TAManagerApp initApp) {
        // KEEP THIS FOR LATER
        app = initApp;

        // CONSTRUCT THE LIST OF TAs FOR THE TABLE
        teachingAssistants = FXCollections.observableArrayList();

        // THESE ARE THE DEFAULT OFFICE HOURS
        startHour = MIN_START_HOUR;
        endHour = MAX_END_HOUR;
        
        //THIS WILL STORE OUR OFFICE HOURS
        officeHours = new HashMap();
        
        // THESE ARE THE LANGUAGE-DEPENDENT OFFICE HOURS GRID HEADERS
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        try {
        		props.loadProperties(XML_PROPERTIES_FILE_NAME,XML_SCHEMA_FILE_NAME);
        		}
        catch(InvalidXMLFileFormatException exp) {
        		AppMessageDialogSingleton dialog = djf.ui.AppMessageDialogSingleton.getSingleton();
        		dialog.show(LOADING_XML_MSG_TITLE,LOADING_XML_FILE_ERROR_MSG);
        		
        	}
        ArrayList<String> timeHeaders = props.getPropertyOptionsList(TAManagerProp.OFFICE_HOURS_TABLE_HEADERS);
        ArrayList<String> dowHeaders = props.getPropertyOptionsList(TAManagerProp.DAYS_OF_WEEK);
        gridHeaders = new ArrayList();
        gridHeaders.addAll(timeHeaders);
        gridHeaders.addAll(dowHeaders);
    }
    
    /**
     * Called each time new work is created or loaded, it resets all data
     * and data structures such that they can be used for new values.
     */
    @Override
    public void resetData() {
        startHour = MIN_START_HOUR;
        endHour = MAX_END_HOUR;
        teachingAssistants.clear();
        officeHours.clear();
    }
    
    // ACCESSOR METHODS

    public int getStartHour() {
        return startHour;
    }

    public int getEndHour() {
        return endHour;
    }
    
    public ArrayList<String> getGridHeaders() {
        return gridHeaders;
    }

    public ObservableList getTeachingAssistants() {
        return teachingAssistants;
    }
    
    public String getCellKey(int col, int row) {
        return col + "_" + row;
    }

    public StringProperty getCellTextProperty(int col, int row) {
        String cellKey = getCellKey(col, row);
        return officeHours.get(cellKey);
    }

    public HashMap<String, StringProperty> getOfficeHours() {
        return officeHours;
    }
    
    public int getNumRows() {
        return ((endHour - startHour) * 2) + 1;
    }

    public String getTimeString(int militaryHour, boolean onHour) {
        String minutesText = "00";
        if (!onHour) {
            minutesText = "30";
        }

        // FIRST THE START AND END CELLS
        int hour = militaryHour;
        if (hour > 12) {
            hour -= 12;
        }
        String cellText = "" + hour + ":" + minutesText;
        if (militaryHour < 12) {
            cellText += "am";
        } else {
            cellText += "pm";
        }
        return cellText;
    }
    
    public String getCellKey(String day, String time) {
        int col = gridHeaders.indexOf(day);
        int row = 1;
        int hour = Integer.parseInt(time.substring(0, time.indexOf("_")));
        int milHour = hour;
        if (hour < startHour)
            milHour += 12;
        row += (milHour - startHour) * 2;
        if (time.contains("_30"))
            row += 1;
        return getCellKey(col, row);
    }
    
    public TeachingAssistant getTA(String testName) {
        for (TeachingAssistant ta : teachingAssistants) {
            if (ta.getName().equals(testName)) {
                return ta;
            }
        }
        return null;
    }
    
    /**
     * This method is for giving this data manager the string property
     * for a given cell.
     */
    public void setCellProperty(int col, int row, StringProperty prop) {
        String cellKey = getCellKey(col, row);
        officeHours.put(cellKey, prop);
    }    
    
    /**
     * This method is for setting the string property for a given cell.
     */
    public void setGridProperty(ArrayList<ArrayList<StringProperty>> grid,
                                int column, int row, StringProperty prop) {
        grid.get(row).set(column, prop);
    }
    
    private void initOfficeHours(int initStartHour, int initEndHour) {
        // NOTE THAT THESE VALUES MUST BE PRE-VERIFIED
        startHour = initStartHour;
        endHour = initEndHour;
        
        // EMPTY THE CURRENT OFFICE HOURS VALUES
        officeHours.clear();
            
        // WE'LL BUILD THE USER INTERFACE COMPONENT FOR THE
        // OFFICE HOURS GRID AND FEED THEM TO OUR DATA
        // STRUCTURE AS WE GO
        TAWorkspace workspaceComponent = (TAWorkspace)app.getWorkspaceComponent();
        workspaceComponent.reloadOfficeHoursGrid(this);
    }
    
    public void initHours(String startHourText, String endHourText) {
        int initStartHour = Integer.parseInt(startHourText);
        int initEndHour = Integer.parseInt(endHourText);
        if ((initStartHour >= MIN_START_HOUR)
                && (initEndHour <= MAX_END_HOUR)
                && (initStartHour <= initEndHour)) {
            // THESE ARE VALID HOURS SO KEEP THEM
            initOfficeHours(initStartHour, initEndHour);
        }
    }

    public boolean containsTA(String testName) {
        for (TeachingAssistant ta : teachingAssistants) {
            if (ta.getName().equals(testName)) {
                return true;
            }
        }
        return false;
    }

    public void addTA(String initName, String email) {
        // MAKE THE TA
        TeachingAssistant ta = new TeachingAssistant(initName,email);

        // ADD THE TA
        if (!containsTA(initName)) {
            teachingAssistants.add(ta);
        }

        // SORT THE TAS
        Collections.sort(teachingAssistants);
    }
    
    // ADD TeachingAssistant INSTANCE TO THE LIST
    // TO BE ABLE TO USE UNDO/REDO THIS METHOND MUST BE CALLED WHEN ADDING A NEW TA
    public void addTA(TeachingAssistant ta) {
    	if(!containsTA(ta.getName())) {
    		teachingAssistants.add(ta);
    	}
    }
    
   public void editTA(TeachingAssistant ta, String newName,String newEmail) {
	   String oldName = ta.getName();
	   for (TeachingAssistant testTA : teachingAssistants) {
		   if (testTA.compareTo(ta) == 0){
			   testTA.setName(newName);
			   testTA.setEmail(newEmail);
			   break;
		   }
	   }
	   Collections.sort(teachingAssistants);
	   updateNameInGridPane(oldName,newName);
	   
   }
   
   public void updateNameInGridPane(String oldName,String newName) {
	   Collection<StringProperty> collection = officeHours.values();
	   for (StringProperty prop : collection) {
		   String newString = prop.getValue().replaceAll(oldName, newName);
		   prop.setValue(newString);
	   }
   }

    public void addOfficeHoursReservation(String day, String time, String taName) {
        String cellKey = getCellKey(day, time);
        toggleTAOfficeHours(cellKey, taName);
    }
    
    /**
     * This function toggles the taName in the cell represented
     * by cellKey. Toggle means if it's there it removes it, if
     * it's not there it adds it.
     */
    public void toggleTAOfficeHours(String cellKey, String taName) {
        StringProperty cellProp = officeHours.get(cellKey);
        String cellText = cellProp.getValue();
        if(cellText.indexOf(taName)==-1)
        		cellText = cellText + "\n" + taName;
        else
        		cellText = cellText.replace(taName,"").replaceAll("(?m)^[ \t]*\r?\n", "");
        cellProp.setValue(cellText);
        		
    }
    
    /**
     * This method removes taName from the office grid cell
     * represented by cellProp.
     */
    public void removeTAFromCell(StringProperty cellProp, String taName) {
        // GET THE CELL TEXT
        String cellText = cellProp.getValue();
        // IS IT THE ONLY TA IN THE CELL?
        if (cellText.equals(taName)) {
            cellProp.setValue("");
        }
        // IS IT THE FIRST TA IN A CELL WITH MULTIPLE TA'S?
        else if (cellText.indexOf(taName) == 0) {
            int startIndex = cellText.indexOf("\n") + 1;
            cellText = cellText.substring(startIndex);
            cellProp.setValue(cellText);
        }
        // IT MUST BE ANOTHER TA IN THE CELL
        else{
            int startIndex = cellText.indexOf("\n" + taName);
            if(startIndex>=0) {
            cellText = cellText.substring(0, startIndex);
            cellProp.setValue(cellText);
            }
        }
    }
    
    public boolean removeTeachingAssistant(TeachingAssistant ta) {
    		teachingAssistants.remove(ta);
    		for (StringProperty stringProperty : officeHours.values()){
    			removeTAFromCell(stringProperty, ta.getName());
    		}
    		return true;
    		
    }
}
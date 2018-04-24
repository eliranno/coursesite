package tam.workspace;

import static tam.TAManagerProp.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import djf.ui.AppMessageDialogSingleton;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import properties_manager.PropertiesManager;
import tam.TAManagerApp;
import tam.data.TAData;
import tam.data.TeachingAssistant;
import tam.workspace.TAWorkspace;

/**
 * This class provides responses to all workspace interactions, meaning
 * interactions with the application controls not including the file
 * toolbar.
 * 
 * @author Richard McKenna
 * @version 1.0
 */
public class TAController {
    // THE APP PROVIDES ACCESS TO OTHER COMPONENTS AS NEEDED
    TAManagerApp app;

    /**
     * Constructor, note that the app must already be constructed.
     */
    public TAController(TAManagerApp initApp) {
        // KEEP THIS FOR LATER
        app = initApp;
    }
    
    /**
     * This method responds to when the user requests to add
     * a new TA via the UI. Note that it must first do some
     * validation to make sure a unique name and email address
     * has been provided.
     */
    public void handleAddTA() {
        // WE'LL NEED THE WORKSPACE TO RETRIEVE THE USER INPUT VALUES
        TAWorkspace workspace = (TAWorkspace)app.getWorkspaceComponent();
        TextField nameTextField = workspace.getNameTextField();
        TextField emailTextField = workspace.getEmailTextField();
        String name = nameTextField.getText();
        String email = emailTextField.getText();
        
        // WE'LL NEED TO ASK THE DATA SOME QUESTIONS TOO
        TAData data = (TAData)app.getDataComponent();
        
        // WE'LL NEED THIS IN CASE WE NEED TO DISPLAY ANY ERROR MESSAGES
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        
        // DID THE USER NEGLECT TO PROVIDE A TA NAME?
        if (name.isEmpty()) {
	    AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
	    dialog.show(props.getProperty(MISSING_TA_NAME_TITLE), props.getProperty(MISSING_TA_NAME_MESSAGE));            
        }
        // DOES A TA ALREADY HAVE THE SAME NAME OR EMAIL?
        else if (data.containsTA(name)) {
	    AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
	    dialog.show(props.getProperty(TA_NAME_AND_EMAIL_NOT_UNIQUE_TITLE), props.getProperty(TA_NAME_AND_EMAIL_NOT_UNIQUE_MESSAGE));                                    
        }
        else if (Pattern.compile( "[0-9]" ).matcher( name ).find()) {
    	    AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
    	    dialog.show(props.getProperty(INVALID_TA_NAME), props.getProperty(INVALID_TA_NAME));
        }
        // EVERYTHING IS FINE, ADD A NEW TA
        else {
            // ADD THE NEW TA TO THE DATA
            data.addTA(name,email);
            app.getGUI().markWorkspaceAsEdited();
            
            // CLEAR THE TEXT FIELDS
            nameTextField.setText("");
            
            // AND SEND THE CARET BACK TO THE NAME TEXT FIELD FOR EASY DATA ENTRY
            nameTextField.requestFocus();
        }
    }
    
    public void handleEditTA() {
    	TAWorkspace workspace = (TAWorkspace)app.getWorkspaceComponent();
        TeachingAssistant currentTA = (TeachingAssistant) workspace.getTATable().getSelectionModel().getSelectedItem();
        TextField nameTextField = workspace.getNameTextField();
        TextField emailTextField = workspace.getEmailTextField();
        String newName = nameTextField.getText();
        String newEmail = emailTextField.getText();
        TAData data = (TAData)app.getDataComponent();
        data.editTA(currentTA, newName, newEmail);
    }

    /**
     * This function provides a response for when the user clicks
     * on the office hours grid to add or remove a TA to a time slot.
     * 
     * @param pane The pane that was toggled.
     */
    public void handleCellToggle(Pane pane) {
        // GET THE TABLE
        TAWorkspace workspace = (TAWorkspace)app.getWorkspaceComponent();
        TableView taTable = workspace.getTATable();
        
        // IS A TA SELECTED IN THE TABLE?
        Object selectedItem = taTable.getSelectionModel().getSelectedItem();
        
        // GET THE TA
        TeachingAssistant ta = (TeachingAssistant)selectedItem;
        if(ta!=null) {
        		String taName = ta.getName();
        		TAData data = (TAData)app.getDataComponent();
        		String cellKey = pane.getId();
        		// AND TOGGLE THE OFFICE HOURS IN THE CLICKED CELL
        		data.toggleTAOfficeHours(cellKey, taName);
        		app.getGUI().markWorkspaceAsEdited();
        }
    }
        
    public void deleteTA(TeachingAssistant ta) {
    		TAData data = (TAData)app.getDataComponent();
			data.removeTeachingAssistant(ta);
    }
    
    public TeachingAssistant getNextTeachingAssistant(TeachingAssistant ta) {
		TAData data = (TAData) app.getDataComponent();
		int index = data.getTeachingAssistants().indexOf(ta);
		int size = data.getTeachingAssistants().size();
		int nextIndex = index+1 >=size ? 0 : index+1;
		return (TeachingAssistant) data.getTeachingAssistants().get(nextIndex);
}
    public TeachingAssistant getPreviousTeachingAssistant(TeachingAssistant ta) {
		TAData data = (TAData) app.getDataComponent();
		int index = data.getTeachingAssistants().indexOf(ta);
		int size = data.getTeachingAssistants().size();
		int previousIndex = index-1 < 0 ? size-1 : index-1;
		return (TeachingAssistant) data.getTeachingAssistants().get(previousIndex);
}
}
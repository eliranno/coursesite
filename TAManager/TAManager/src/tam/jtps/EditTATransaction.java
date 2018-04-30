package tam.jtps;

import tam.jtps.jTPS_Transaction;
import tam.data.TAData;
import tam.data.TeachingAssistant;

public class EditTATransaction implements jTPS_Transaction {
	
	String originalName;
	String originalEmail;
	String newName;
	String newEmail;
	TeachingAssistant ta;
	TAData dataComponent;
	
	public EditTATransaction(TeachingAssistant ta , String newName,String newEmail,TAData dataComponent) {
		this.originalEmail =ta.getEmail();
		this.originalName = ta.getName();
		this.dataComponent = dataComponent;
		this.ta = ta;
		this.newName = newName;
		this.newEmail = newEmail;
	}

	@Override
	public void doTransaction() {
		dataComponent.editTA(ta, newName, newEmail);
	}

	@Override
	public void undoTransaction() {
		dataComponent.editTA(ta, originalName,originalEmail);
	}

}

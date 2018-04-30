package tam.jtps;
import tam.data.TAData;
import tam.data.TeachingAssistant;
import tam.workspace.*;

public class AddTATranscation implements jTPS_Transaction {
	
	TeachingAssistant ta;
	TAData dataComponent;
	
	public AddTATranscation(TeachingAssistant ta, TAData data) {
		this.ta = ta;
		dataComponent = data;
	}

	@Override
	public void doTransaction() {
		this.dataComponent.addTA(ta);
	}

	@Override
	public void undoTransaction() {
		this.dataComponent.removeTeachingAssistant(ta);
	}

}

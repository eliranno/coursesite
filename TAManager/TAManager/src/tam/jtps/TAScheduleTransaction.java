package tam.jtps;

import javafx.scene.layout.Pane;
import tam.data.TAData;
import tam.jtps.jTPS_Transaction;

public class TAScheduleTransaction implements jTPS_Transaction {
	
	String taName;
	String cellKey;
	TAData dataComponent;
	
	public TAScheduleTransaction(String cellKey,String name,TAData dataComponent) {
		this.taName = name;
		this.cellKey = cellKey;
		this.dataComponent = dataComponent;
		
	}

	@Override
	public void doTransaction() {
		dataComponent.toggleTAOfficeHours(cellKey, taName);
	}

	@Override
	public void undoTransaction() {
		dataComponent.toggleTAOfficeHours(cellKey, taName);
	}

}

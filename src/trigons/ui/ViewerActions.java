package trigons.ui;

import java.awt.event.ActionEvent;

import vordeka.util.swing.AbstractActionPlus;
import vordeka.util.swing.ActionPlus;

public class ViewerActions {
	public final ActionPlus toggleEditMode;
	
	
	public final AppFramework framework;

	public ViewerActions(AppFramework framework) {
		this.framework = framework;
		this.toggleEditMode = new AToggleEditMode();
	}
	
	public void init() {
		// TODO Auto-generated method stub
		
	}
	
	public class AToggleEditMode extends AbstractActionPlus {
		private static final long serialVersionUID = -7677130262791029765L;

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if(framework.isEditModeOn()){
					framework.exitEditMode();
				} else {
					framework.enterEditMode();
				}
			} finally {
				
			}
		}
		
	
	}

	
}

package vordeka.util.swing;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.KeyStroke;

public abstract class AbstractActionPlus extends AbstractAction implements ActionPlus {
	private static final long serialVersionUID = 2229159931861173495L;

	public AbstractActionPlus(){
		
	}
	
	//	I could offer so many different constructor variations. But I chose to offer just the most commonly useful.
	
	/**
	 * Initializes this action's <code>NAME</code>,<code>ACTION_COMMAND</code>,
	 * <code>LONG_DESCRIPTION</code>, and <code>SHORT_DESCRIPTION</code> properties. 
	 * @param name
	 * 		the value of this action's <code>NAME</code> and <code>ACTION_COMMAND</code> properties
	 * @param description
	 * 		the value of this action's <code>LONG_DESCRIPTION</code> and 
	 * 	<code>SHORT_DESCRIPTION</code> properties
	 */
	public AbstractActionPlus(String name, String description){
		this.setName(name);
		this.setActionCommand(name);
		this.setShortDescription(description);
		this.setLongDescription(description);
	}
	
	/**
	 * Initializes this action's <code>NAME</code>,<code>ACTION_COMMAND</code>,
	 * <code>LONG_DESCRIPTION</code>, and <code>SHORT_DESCRIPTION</code> properties. 
	 * @param name
	 * 		the value of this action's <code>NAME</code> and <code>ACTION_COMMAND</code> properties
	 * @param shortDescription
	 * 		the value of this action's <code>SHORT_DESCRIPTION</code> property
	 * @param longDescription
	 * 		the value of this action's <code>LONG_DESCRIPTION</code> property
	 */
	public AbstractActionPlus(String name, String shortDescription, String longDescription){
		this.setName(name);
		this.setActionCommand(name);
		this.setShortDescription(shortDescription);
		this.setLongDescription(longDescription);
	}
	
	/**
	 * Initializes this action's <code>NAME</code>,<code>ACTION_COMMAND</code>,
	 * <code>LONG_DESCRIPTION</code>, <code>SHORT_DESCRIPTION</code>, <code>SMALL_ICON</code> 
	 * and <code>LARGE_ICON</code> properties. 
	 * @param name
	 * 		the value of this action's <code>NAME</code> and <code>ACTION_COMMAND</code> properties
	 * @param shortDescription
	 * 		the value of this action's <code>SHORT_DESCRIPTION</code> property
	 * @param longDescription
	 * 		the value of this action's <code>LONG_DESCRIPTION</code> property
	 * @param icon
	 * 		the value of this action's <code>SMALL_ICON</code> and <code>LARGE_ICON</code> properties
	 */
	public AbstractActionPlus(String name, String shortDescription, String longDescription, Icon icon){
		this.setName(name);
		this.setActionCommand(name);
		this.setShortDescription(shortDescription);
		this.setLongDescription(longDescription);
		this.setSmallIcon(icon);
		this.setLargeIcon(icon);
	}
	
	/**
	 * Initializes this action's <code>NAME</code>,<code>ACTION_COMMAND</code>,
	 * <code>LONG_DESCRIPTION</code>, <code>SHORT_DESCRIPTION</code>, <code>SMALL_ICON</code>, 
	 * <code>LARGE_ICON</code>, <code>ACCELERATOR</code>, <code>MNEMONIC</code>, <code>DISPLAYED_MNEMONIC_INDEX</code>,
	 *  properties. 
	 * @param name
	 * 		the value of this action's <code>NAME</code> property
	 * @param actionCommand
	 * 		the value of this action's <code>ACTION_COMMAND</code> property
	 * @param shortDescription
	 * 		the value of this action's <code>SHORT_DESCRIPTION</code> property
	 * @param longDescription
	 * 		the value of this action's <code>LONG_DESCRIPTION</code> property
	 * @param smallIcon
	 * 		the value of this action's <code>SMALL_ICON</code> property
	 * @param largeIcon
	 * 		the value of this action's <code>LARGE_ICON</code> property
	 * @param accelerator
	 * 		the value of this action's <code>ACCELERATOR</code> property
	 * @param mnemonic
	 * 		the value of this action's <code>MNEMONIC</code> property
	 * @param displayedMnemonicIndex
	 * 		the value of this action's <code>DISPLAYED_MNEMONIC_INDEX</code> property
	 */
	public AbstractActionPlus(String name, String actionCommand, String shortDescription, 
			String longDescription, Icon smallIcon, Icon largeIcon, KeyStroke accelerator,
			int mnemonic, int displayedMnemonicIndex){
		
	}
	
	
	public String getName(){
		return (String) this.getValue(NAME);
	}
	
	
	public KeyStroke getAccelerator(){
		return (KeyStroke) this.getValue(ACCELERATOR_KEY);
	}
	
	
	public String getActionCommand(){
		return (String) this.getValue(ACTION_COMMAND_KEY);
	}
	
	
	public Integer getMnemonic(){
		return (Integer) this.getValue(MNEMONIC_KEY);
	}
	
	
	public Integer getDisplayedMnemonicIndex(){
		return (Integer) this.getValue(DISPLAYED_MNEMONIC_INDEX_KEY);
	}
	
	
	public String getShortDescription(){
		return (String) this.getValue(SHORT_DESCRIPTION);
	}
	
	public String getLongDescription(){
		return (String) this.getValue(LONG_DESCRIPTION);
	}
	
	
	public Icon getSmallIcon(){
		return (Icon) this.getValue(SMALL_ICON);
	}
	
	public Icon getLargeIcon(){
		return (Icon) this.getValue(LARGE_ICON_KEY);
	}
	
	
	public Boolean isSelected(){
		return (Boolean) this.getValue(SELECTED_KEY);
	}
	
	
	public void setName(String name){
		putValue(NAME, name);
	}
	
	
	public void setAcceleratorKey(KeyStroke acceleratorKey){
		putValue(ACCELERATOR_KEY, acceleratorKey);
	}

	
	public void setActionCommand(String actionCommand){
		putValue(ACTION_COMMAND_KEY, actionCommand);
	}

	
	public void setMnemonic(Integer mnemonic){
		putValue(MNEMONIC_KEY, mnemonic);
	}

	
	public void setDisplayedMnemonicIndex(Integer displayedMnemonicIndex){
		putValue(DISPLAYED_MNEMONIC_INDEX_KEY, displayedMnemonicIndex);
	}

	
	public void setShortDescription(String shortDescription){
		putValue(SHORT_DESCRIPTION, shortDescription);
	}
	
	public void setLongDescription(String longDescription){
		putValue(LONG_DESCRIPTION, longDescription);
	}

	
	public void setSmallIcon(Icon smallIcon){
		putValue(SMALL_ICON, smallIcon);
	}
	
	public void setLargeIcon(Icon largeIcon){
		putValue(LARGE_ICON_KEY, largeIcon);
	}

	
	public void setSelected(Boolean selected){
		putValue(SELECTED_KEY, selected);
	}
}

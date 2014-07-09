package vordeka.util.swing;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.KeyStroke;

public interface ActionPlus extends Action {

	String getName();

	KeyStroke getAccelerator();

	String getActionCommand();

	/**
	 * Gets the KeyEvent constant used as a mnemonic for this action. 
	 * This value should correspond to one of the KeyEvent key codes. 
	 * @return
	 * 		a numerical key constant
	 * @see {@linkplain #setMnemonic(Integer)}
	 */
	Integer getMnemonic();

	Integer getDisplayedMnemonicIndex();

	String getShortDescription();

	String getLongDescription();

	Icon getSmallIcon();

	Icon getLargeIcon();

	Boolean isSelected();

	void setName(String name);

	void setAcceleratorKey(KeyStroke acceleratorKey);

	void setActionCommand(String actionCommand);

	/**
	 * Sets the KeyEvent constant used as a mnemonic for this action. 
	 * The value should correspond to one of the KeyEvent key codes. 
	 * For example: myAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A)
	 * sets the mnemonic of myAction to 'a'.
	 * @param mnemonic
	 * 		a numerical key constant
	 */
	void setMnemonic(Integer mnemonic);

	void setDisplayedMnemonicIndex(Integer displayedMnemonicIndex);

	void setShortDescription(String shortDescription);

	void setLongDescription(String longDescription);

	void setSmallIcon(Icon smallIcon);

	void setLargeIcon(Icon largeIcon);

	void setSelected(Boolean selected);
}

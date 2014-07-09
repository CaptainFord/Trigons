/**
 * 
 */
package vordeka.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import vordeka.util.collection.ArrayGroup;


/**
 * @author Alex
 *
 */
public class SettingsSaver {

	public static SettingsSaver saver;
	private static final Map<String, SettingsSaver> savers = new HashMap<String, SettingsSaver>();
	
	private final File file;
	private final Properties settings;
	
	private final ArrayGroup<SettingsSaverListener> listeners = 
			new ArrayGroup<SettingsSaverListener>();
	private final Map<String,ArrayGroup<SettingsSaverListener>> settingListeners = 
			new HashMap<String,ArrayGroup<SettingsSaverListener>>();
	
	protected SettingsSaver(File settingsFile){
		file = settingsFile;
                settings = new Properties();
		if(file.exists()){
			FileInputStream fis;
			try {
				fis = new FileInputStream(file);
				settings.load(fis);
				fis.close();
			} catch (FileNotFoundException e) {
				ErrUtil.errMsg(e, "Trying to load saved preferences");
			} catch (IOException e) {
				ErrUtil.errMsg(e, "Trying to load saved preferences");
			}
		}
	}
	
	public static File getUserHome(){
		return new File(System.getProperty("user.home"));
	}
	
	/**
	 * Simply makes use of the method that SettingsSaver uses to find the 
	 * user's home directory.
	 * @param folderName
	 * @param filename
	 * @return
	 */
    public static File getSettingsFile(String folderName, String filename){
    	if(!filename.contains("."))
			filename += ".props";
    	File folder = new File(getUserHome(), folderName);
    	File file = new File(folder, filename);
    	return file;
    }
    
	public static SettingsSaver initSaver(String folderName, String filename) {
		if(!filename.contains("."))
			filename += ".props";
		String key = (folderName + '/' + filename).toLowerCase();
		SettingsSaver saver = savers.get(key);
		if(saver == null){
			File folder = new File(getUserHome(), folderName);
			File file = new File(folder, filename);
			saver = new SettingsSaver(file);
			savers.put(key, saver);
		}
		return saver;
	}
	
	/**
	 * Initializes the static SettingsSaver to a specific file.
	 * Has no effect if the SettingsSaver is already open.
	 * @param settingsFile
	 * 		The file to read and store settings in
	 * @return
	 * 		true if the saver was initialized, 
	 * 	false if it was already open
	 * @throws IllegalArgumentException
	 * 		if the provided File is null or could not be loaded
	 */
    public static SettingsSaver initSaver(File settingsFile){
    	if(settingsFile == null) throw new IllegalArgumentException("settings file may not be null");
        if(saver == null){
            saver = new SettingsSaver(settingsFile);
        }
    	return saver;
    }

    /**
	 * Initializes the static SettingsSaver to a specific file.
	 * Has no effect if the SettingsSaver is already open.
	 * @param settingsFile
	 * 		The file to read and store settings in
	 * @return
	 * 		true if the saver was initialized, 
	 * 	false if it was already open
	 * @throws IllegalArgumentException
	 * 		if the provided folderName is null or empty
	 */
    public static SettingsSaver initSaver(String folderName){
    	if(folderName == null || folderName.trim().length() == 0) throw new IllegalArgumentException("settings folder name may not be null or empty");
        if(saver == null){
        	saver = new SettingsSaver(new File(getUserHome() + 
        			"/" + folderName + "/User Preferences.props"));
        }
        return saver;
    }
    
    ////
    //	STATIC METHODS END HERE
    ////
    //	INSTANCE METHODS BEGIN HERE
    ////

    /**
     * All the saveSetting methods are masks to this method call. This
     * <em>greatly</em> simplifies the firing of events, since I only need
     * to handle it in one place. Since all values are stored as strings 
     * internally, and all other methods simply perform conversion, there's
     * no issue with this.
     * @param key
     * 		the name of the setting
     * @param value
     * 		the value of the setting
     */
    protected void put(String key, String value){
    	Object oldValue = settings.setProperty(key, value);
    	//	Now it doesn't fire an event if the value of the setting didn't change
    	if(oldValue != value && !(oldValue != null && oldValue.equals(value))){
        	fireSettingChanged(key);
    	}
    }

	private void fireSettingChanged(String key) {
		SettingsSaverListener[] listeners = this.listeners.toArray(new SettingsSaverListener[this.listeners.size()]);
		for(SettingsSaverListener l : listeners){
			l.settingChanged(this, key);
		}
		ArrayGroup<SettingsSaverListener> group = settingListeners.get(key);
		if(group != null){
			listeners = group.toArray(new SettingsSaverListener[group.size()]);
			for(SettingsSaverListener l : listeners){
				l.settingChanged(this, key);
			}
		}
	}

	/**
	 * Commits the current settings to a file, ready to be loaded the
	 * next time this program starts up.
	 */
	public void saveSettings(){
		try{
			if(!file.exists()){
				file.getParentFile().mkdirs();
			}
			FileOutputStream fos = new FileOutputStream(file);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			settings.store(bos, "User Settings and Preferences");
			bos.flush();
			fos.flush();
			bos.close();
			fos.close();
		} catch(Exception e){
			ErrUtil.errMsg(e, "Trying to save preferences");
		}
	}
	
	
	public int getIntSetting(String key, int defaultValue){
		String value = settings.getProperty(key);
		if(value == null) return defaultValue;
		return Integer.parseInt(value);
	}
	
	public boolean getBooleanSetting(String key, boolean defaultValue){
		String value = settings.getProperty(key);
		if(value == null) return defaultValue;
		return Boolean.parseBoolean(value);
	}
	public String getSetting(String key){
		return settings.getProperty(key);
	}
	
	
	public void saveSetting(String key, int value){
		put(key, Integer.toString(value));
	}
	
	public void saveSetting(String key, boolean value){
		put(key, Boolean.toString(value));
	}
	
	public void saveSetting(String key, String value){
		put(key, value);
	}

	/**
	 * Returns the file saved under the specified key, or null
	 * if the setting does not exist.
	 * @param key
	 * @return
	 */
	public File getFileSetting(String key) {
		String value = settings.getProperty(key);
		if(value == null) return null;
		return new File(value);
	}
	
	/**
	 * Returns the file saved under the specified key, or the specified
	 * default value if the setting does not exist.
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public File getFileSetting(String key, String defaultValue) {
		return settings.containsKey(key) 
				? getFileSetting(key) 
				: (defaultValue == null ? null : new File(defaultValue));
	}
	
	/**
	 * Returns the file saved under the specified key, or the specified
	 * default value if the setting does not exist.
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public File getFileSetting(String key, File defaultValue) {
		return settings.containsKey(key) 
				? getFileSetting(key) 
				: defaultValue;
	}
	


	public File getOrCreateFileSetting(String key, String defaultValue) {
		if(settings.containsKey(key)){
			return getFileSetting(key);
		} else {
			settings.setProperty(key, defaultValue);
			return defaultValue == null ? null : new File(defaultValue);
		}
	}
	
	public File getOrCreateFileSetting(String key, File defaultValue) {
		if(settings.containsKey(key)){
			return getFileSetting(key);
		} else {
			settings.setProperty(key, defaultValue.getPath());
			return defaultValue == null ? null : defaultValue;
		}
	}
	
	/**
	 * Saves a file's absolute path so that it may be later
	 * restored by using getFileSetting(String)
	 * @param string
	 * @return
	 */
	public void saveSetting(String key, File value) {
		settings.setProperty(key, value.getAbsolutePath());
	}
	
	public void saveSetting(String key, Collection<File> fileList){
		//	Will use '| ' to seperate, since that character is illegal in windows pathnames
		//	and not used in any sort of path that I know of (':' is used in drive lettering)
		//	The space is in the rare occurence that a value begins with '|', so that it
		//	does not displace the '|' to the end of the previous file.
		StringBuilder value = new StringBuilder();
		for(File file : fileList){
			String path = file.getAbsolutePath();
			value.append(path.replace("|", "||")).append("| ");
		} 
		value.setLength(value.length() - 2);
		
		//	Changed to calling another version of this method to simplify the firing of events,
		//	since this is ultimately a string value.
		saveSetting(key, value.toString());
//		settings.put(key, value.toString());
	}
	
	/**
	 * @param string
	 */
	public List<File> getFileListSetting(String key) {
		String value = settings.getProperty(key);
		String path;
		ErrUtil.debugMsg("Parsing File List from: " + value);
		if(value == null || value.length() == 0) return Collections.emptyList();
		int startIndex = 0;
		int divider = value.indexOf('|');
		List<File> retval = new ArrayList<File>();
		while(divider != -1){
			if(value.charAt(divider + 1) == '|'){
				// double '|' will be subsequently replaced with singles
				// bizarre as it may be, they belong in the pathname
				++divider;
			} else {
				path = value.substring(startIndex, divider).replace("||", "|");
				try{
					retval.add(new File(path));
				} catch (Exception e){
					ErrUtil.errMsg(e, "loading FileList setting '" + key +"' from path: " + path);
				}
				startIndex = divider + 2;
			}
			divider = value.indexOf(divider + 1, '|');
		}
		path = value.substring(startIndex).replace("||", "|");
		try{
			retval.add(new File(path));
		} catch (Exception e){
			ErrUtil.errMsg(e, "loading FileList setting '" + key +"' from path: " + path);
		}
		return retval;
	}
	public String getSetting(String key, String defaultValue) {
		String value = settings.getProperty(key);
		if(value == null) return defaultValue;
		return value;
	}

	public boolean hasSetting(String key) {
		return settings.containsKey(key);
	}
	
	public void addSettingListener(SettingsSaverListener l){
		if(l == null) return;
		listeners.add(l);
	}
	public void removeSettingListener(SettingsSaverListener l){
		if(l == null) return;
		listeners.remove(l);
	}
	
	public void addSettingListener(String settingName, SettingsSaverListener l){
		if(l == null) return;
		ArrayGroup<SettingsSaverListener> group = settingListeners.get(settingName);
		if(group == null){
			group = new ArrayGroup<SettingsSaverListener>();
			settingListeners.put(settingName, group);
		}
		group.add(l);
	}
	public void removeSettingListener(String settingName, SettingsSaverListener l){
		if(l == null) return;
		ArrayGroup<SettingsSaverListener> group = settingListeners.get(settingName);
		if(group == null) return;
		group.remove(l);
	}

	public File getSaverFolder() {
		return this.file.getParentFile();
	}

	public void deleteSetting(String key) {
		if(this.settings.containsKey(key)){
			this.settings.remove(key);
			this.fireSettingChanged(key);
		}
	}
}

package xbrlcore.instance;

import java.io.File;

/**
 * This class is used to be extended in order to load files from other location than
 * the "Instance directory".
 *
 * @author Nicolas Georges, Sébastien Kirche, SantosEE
 */
public class FileLoader {
	public File load(String filePath, String fileName){
		File loadedFile = null;
		loadedFile = new File(filePath + fileName);
		return loadedFile;
	}
	
	public String getFullPathName( String filePath, String fileName){
		return filePath + fileName ;
	}
	
	/**
	 * Entry point for a cache management system
	 * The object to cache could be saved e.g. on disk or in a database
	 * TODO: provide a example of cache, maybe the CW gzipped format 
	 * 
	 * @param method : load, save to get / set an object from the cache
	 * @param object : the object to cache
	 * @param key : a key to use while caching the object
	 * @return 
	 */
	public Object cacheRequest(String method, Object object, String key){		
		return object;
	}	
}

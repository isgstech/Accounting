package TaxonomyBrowser;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * 
 * @author Nicolas Georges
 *
 */
public class TaxonomyFilter extends FileFilter {
	
	@Override
	public boolean accept(File file) {
		if (file.isDirectory()){
			return true;
		}
		
		if( file.getAbsolutePath().matches("^.+t-.+[.]xsd$")){
			return true;
		}
		
		return false;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Taxonomy Files (t-*.xsd)";
	}

}

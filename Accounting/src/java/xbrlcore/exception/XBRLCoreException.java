package xbrlcore.exception;

import org.xml.sax.SAXException;

/**
 * Top-Level exception.
 *  
 */
public class XBRLCoreException extends SAXException {
	
    static final long serialVersionUID = -3321715930220111762L;

    public XBRLCoreException(String msg) {
        super(msg);
    }

}

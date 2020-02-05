package xbrlcore.exception;

/**
 * An exception that is thrown if something with an instance goes wrong. <br/>
 * <br/>
 * 
 * @author SantosEE
 */
public class InstanceException extends Exception {
	
    static final long serialVersionUID = 3219000168674937922L;

    public InstanceException(String message) {
        super(message);
    }
}

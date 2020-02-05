package xbrlcore.linkbase;

import java.util.Iterator;
import java.util.List;

/**
 * 
 * TODO Vielleicht mal über einen allgemeinen Iterator - LinkbaseIterator -
 * nachdenken.
 * 
 * @author SantosEE
 */
public class PresentationLinkbaseIterator implements Iterator<PresentationLinkbaseElement> {

	private List<PresentationLinkbaseElement> elementList;

	private int counter;

	public PresentationLinkbaseIterator(List<PresentationLinkbaseElement> elementList) {
		this.elementList = elementList;
		counter = 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
		/** TODO: not implemented yet * */
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		if (elementList == null)
			return false;
		return counter < elementList.size() ? true : false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#next()
	 */
	public PresentationLinkbaseElement next() {
		return elementList.get(counter++);
	}

}
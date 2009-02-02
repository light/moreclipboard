package moreclipboard;

import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;

/**
 * @author Mikhail Barg
 *
 */
public class MCContents{
	
	private LinkedList<String> m_list;
	private Clipboard m_clipboard;
	private MCView m_view;
	
	public static int MAX_ELEMENTS = 16;
	
	public MCContents(){
		m_list = new LinkedList<String>();
        m_clipboard = new Clipboard(Display.getCurrent());
	}
	
	public void dispose(){
		if (m_clipboard != null) {
			m_clipboard.dispose();
			m_clipboard = null;
		}
	}
	
	
	public void addString(String newString) {
		//remove last element(s) if necessary
		while (m_list.size() >= MAX_ELEMENTS)	{
				m_list.removeLast();
		}
		m_list.addFirst(newString);
		updateView();
	}
	
	public void registerView(MCView view) {
		assert(view != null);
		assert(m_view == null);
		m_view = view;
		updateView();
	}

	public void removeView(MCView view) {
		assert(view != null);
		if (m_view == view)
		{
			m_view = null;
		}
	}

	public String[] getElements() {
		String elements[] = new String[m_list.size()];
		Iterator<String> iterator = m_list.iterator();
		int i = 0;
		while (iterator.hasNext())
		{
			elements[i++] = iterator.next();
		}		
		return elements;
	}
	
	private void updateView() {
		if (m_view == null) {
			return;
		}
		m_view.setElements(getElements());
	}

	public void setCurrentElement(int itemIndex) {
		assert(itemIndex >= 0);
		assert(itemIndex < m_list.size());
		
		// move to the front
		if (itemIndex > 0) {
			m_list.addFirst(m_list.remove(itemIndex));
			updateView();
		}
	}

	public void clear() {
		m_list.clear();
		updateView();
	}

	public void removeElement(int itemIndex) {
		assert(itemIndex >= 0);
		assert(itemIndex < m_list.size());
		m_list.remove(itemIndex);
		updateView();
	}
	
	public void getFromClipboard() {
		TextTransfer textTransfer = TextTransfer.getInstance();
		String string = (String)m_clipboard.getContents(textTransfer);
		if (string != null)
		{
			addString(string);
		}
	}

	public void setToClipboard() {
		if (m_list.isEmpty()) {
			return;
		}
		Object[] transData = new Object[] {m_list.get(0)};
		Transfer[] transfers = new Transfer[]{TextTransfer.getInstance()};
		m_clipboard.setContents(transData, transfers);
	}
}

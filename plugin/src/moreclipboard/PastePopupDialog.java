/**
 *
 */
package moreclipboard;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

/**
 * The main part of the plugin - the pop-up dialog showing the contents of the plugin and allowing pasting
 *
 */
public class PastePopupDialog extends org.eclipse.jface.dialogs.PopupDialog
							  implements SelectionListener, KeyListener
{
	private SashForm m_sashForm;
	private List m_listView;
	private Text m_text;

	public PastePopupDialog(Shell parent)
	{
		super(parent, INFOPOPUPRESIZE_SHELLSTYLE, true, false, false, false, false, null, Messages.PastePopupDialog_infoText);
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		final Composite clientArea = (Composite) super.createDialogArea(parent);

		clientArea.setLayout(new FillLayout());
		m_sashForm = new SashForm(clientArea, SWT.VERTICAL);
		Composite pane1 = new Composite(m_sashForm, SWT.NONE);
		pane1.setLayout(new FillLayout());
		Composite pane2 = new Composite(m_sashForm, SWT.NONE);
		pane2.setLayout(new FillLayout());
		m_sashForm.setWeights(new int[] { 20, 10 });

		m_listView = new List(pane1, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
		m_listView.addSelectionListener(this);
		m_listView.addKeyListener(this);
		m_listView.setItems(Plugin.getInstance().getContents().getElements());
		m_listView.select(0);

		m_text = new Text(pane2, SWT.BEGINNING | SWT.H_SCROLL | SWT.V_SCROLL);

		processSelectionChanged();

		if (Settings.USE_FIXED_WIDTH_FONT)
		{
			Font textFont = JFaceResources.getTextFont();
			m_listView.setFont(textFont);
			m_text.setFont(textFont);
		}
		return clientArea;
	}

	@Override
	protected Point getDefaultSize()
	{
		return new Point (660, 600);
	}

	@Override
	protected java.util.List<Control> getBackgroundColorExclusions()
	{
		java.util.List<Control> exclusions = super.getBackgroundColorExclusions();
		exclusions.add(m_sashForm);
		return exclusions;
	}

	private static void processEvents()
	{
		//see https://bugs.eclipse.org/bugs/show_bug.cgi?id=424576
		//http://sourceforge.net/p/practicalmacro/bugs/17/
	    Display display = PlatformUI.getWorkbench().getDisplay();
	    if (display != null)
	    {
	        while (display.readAndDispatch())
	        {

	        }
	    }
	}

	private void processPasteSelectedElement()
	{
		final int itemIndex = m_listView.getSelectionIndex();
		if (itemIndex < 0)
		{
			return;
		}

		Plugin.getInstance().getContents().setCurrentElement(itemIndex);

		this.close();
		processEvents();	//probably helps to actually assure closing is finished. See links in the method

		try
		{
			PasteHandler.executePaste();
		}
		catch (ExecutionException e)
		{
			//TODO: do something about the exception..
			e.printStackTrace();
		}
	}

	private void processRemoveSelectedElement()
	{
		final int itemIndex = m_listView.getSelectionIndex();
		if (itemIndex < 0)
		{
			return;
		}

		Plugin.getInstance().getContents().removeElement(itemIndex);
		m_listView.setItems(Plugin.getInstance().getContents().getElements());
		m_listView.select(itemIndex > m_listView.getItemCount() - 1 ? itemIndex - 1 : itemIndex);
	}

	private void processSelectionChanged()
	{
		int selectionIndex = m_listView.getSelectionIndex();
		if (selectionIndex != -1)
		{
			String contents = Plugin.getInstance().getContents().getElement(selectionIndex);
			m_text.setText(contents);
		}
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e)
	{
		if (e.widget != m_listView)
		{
			return;
		}

		processPasteSelectedElement();
	}

	@Override
	public void widgetSelected(SelectionEvent e)
	{
		processSelectionChanged();
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		//handling should be in the keyPressed, not keyReleased, to prevent
		// using "Return" key to be handled as a search key in list, which cause a bug..
		if (e.keyCode == SWT.CR
				|| e.keyCode == SWT.KEYPAD_CR
				)
		{
			processPasteSelectedElement();
		}
		else if (e.keyCode == SWT.DEL)
		{
			processRemoveSelectedElement();
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
	}
}

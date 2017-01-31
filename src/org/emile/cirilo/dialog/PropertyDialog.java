package org.emile.cirilo.dialog;

import voodoosoft.jroots.core.gui.CEventListener;
import voodoosoft.jroots.dialog.*;
import voodoosoft.jroots.exception.CException;

import java.awt.event.ActionEvent;

import java.util.*;

import javax.swing.JTable;


/**
 * Description of the Class
 *
 * @author yoda
 * @created 07. September 2006
 */
public class PropertyDialog extends CDialog {
	/**
	 *Constructor for the PropertyTableDialog object
	 */
	public PropertyDialog() {
		moPropertySets = new Vector();
		moPropertyGroups = new Vector();
		moHiddenValues = new Hashtable();
	}


	/**
	 * Adds a feature to the PropertySet attribute of the PropertyTableDialog object
	 *
	 * @param aoProps The feature to be added to the PropertySet attribute
	 * @param asGroup The feature to be added to the PropertySet attribute
	 */
	public void addPropertySet(Properties aoProps, String asGroup) {
		moPropertySets.add(aoProps);
		moPropertyGroups.add(asGroup);
	}


	/**
	 * Description of the Method
	 *
	 * @param e Description of the Parameter
	 */
	public void handleCancelButton(ActionEvent e) {
		close();
	}


	/**
	 * Description of the Method
	 *
	 * @param aoHandler Description of the Parameter
	 */
	public void handlerRemoved(CEventListener aoHandler) {
	}


	/**
	 * Description of the Method
	 *
	 * @param asProperty Description of the Parameter
	 * @param asShowValue Description of the Parameter
	 */
	public void hidePropertyValue(String asProperty, String asShowValue) {
		moHiddenValues.put(asProperty, asShowValue);
	}


	/**
	 * Description of the Method
	 *
	 * @exception CShowFailedException Description of the Exception
	 */
	public void show()
		throws CShowFailedException {
		JTable loTable;

		try {
			moTableModel = new CPropertyTableModel();
			moTableModel.hideValues(moHiddenValues);

			for (int i = 0; i < moPropertySets.size(); i++) {
				moTableModel.addProperties((Properties) moPropertySets.get(i),
					moPropertyGroups.get(i).toString());
			}

			loTable = (JTable) getGuiComposite().getWidget("jtProperties");
			loTable.setModel(moTableModel);
			
			loTable.removeColumn(loTable.getColumnModel().getColumn(0));
			String[] header = {"Property","Value"};
			for(int i=0;i<loTable.getColumnCount();i++) loTable.getTableHeader().getColumnModel().getColumn(i).setHeaderValue(header[i]);

		}
		catch (Exception ex) {
			throw new CShowFailedException(ex);
		}
	}


	/**
	 * Description of the Method
	 */
	protected void cleaningUp() {
		moGA = null;
		moTableModel = null;
	}


	/**
	 * Description of the Method
	 *
	 * @exception COpenFailedException Description of the Exception
	 */
	protected void opened()
		throws COpenFailedException {
		try {
			moGA = (IGuiAdapter) getGuiAdapter();

			CDialogTools.createButtonListener(this, "jbCancel", "handleCancelButton");

			// focus comments
			moGA.requestFocus("jbCancel");
		}
		catch (Exception ex) {
			throw new COpenFailedException(ex);
		}
		finally {
		}
	}


	/**
	 * Description of the Method
	 *
	 * @return Description of the Return Value
	 */
	protected boolean closing() {

		try {


		}
		catch (Exception e) {
			CException.record(e, this, false);
		}

		return true;
	}


	private IGuiAdapter moGA;
	private CPropertyTableModel moTableModel;
	private Vector moPropertySets;
	private Vector moPropertyGroups;
	private Map moHiddenValues;
}


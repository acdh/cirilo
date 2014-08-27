/*
 *  -------------------------------------------------------------------------
 *  Copyright 2014 
 *  Centre for Information Modeling - Austrian Centre for Digital Humanities
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 *  -------------------------------------------------------------------------
 */

package org.emile.cirilo.gui.jtable;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.util.Vector;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;


  public class JSortTable
        extends JTable
        implements MouseListener {

    private static final long serialVersionUID = 1L;

    protected int sortedColumnIndex = -1;

    protected boolean sortedColumnAscending = true;

    public JSortTable() {
        this(new DefaultSortTableModel());
    }

    public JSortTable(int rows, int cols) {
        this(new DefaultSortTableModel(rows, cols));
    }

    public JSortTable(Object[][] data, Object[] names) {
        this(new DefaultSortTableModel(data, names));
    }

    public JSortTable(Vector data, Vector names) {
        this(new DefaultSortTableModel(data, names));
    }

    public JSortTable(SortTableModel model) {
        super(model);
        initSortHeader();
    }

    public JSortTable(SortTableModel model, TableColumnModel colModel) {
        super(model, colModel);
        initSortHeader();
    }

    public JSortTable(SortTableModel model,
                      TableColumnModel colModel,
                      ListSelectionModel selModel) {
        super(model, colModel, selModel);
        initSortHeader();
    }

    protected void initSortHeader() {
        JTableHeader header = getTableHeader();
        header.setDefaultRenderer(new SortHeaderRenderer());
        header.addMouseListener(this);
    }

    public int getSortedColumnIndex() {
        return sortedColumnIndex;
    }

    public boolean isSortedColumnAscending() {
        return sortedColumnAscending;
    }

    public void mouseReleased(MouseEvent event) {
        TableColumnModel colModel = getColumnModel();
        int index = colModel.getColumnIndexAtX(event.getX());
        int modelIndex = colModel.getColumn(index).getModelIndex();

        SortTableModel model = (SortTableModel) getModel();
        if (model.isSortable(modelIndex)) {
            // toggle ascension, if already sorted
            if (sortedColumnIndex == index) {
                sortedColumnAscending = !sortedColumnAscending;
            }
            sortedColumnIndex = index;

            model.sortColumn(modelIndex, sortedColumnAscending);
        }
    }

    public void mousePressed(MouseEvent event) {
    }

    public void mouseClicked(MouseEvent event) {
    }

    public void mouseEntered(MouseEvent event) {
    }

    public void mouseExited(MouseEvent event) {
    }
}

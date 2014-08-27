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

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;


public class SortHeaderRenderer
        extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;

    public static Icon NONSORTED = new SortArrowIcon(SortArrowIcon.NONE);

    public static Icon ASCENDING = new SortArrowIcon(SortArrowIcon.ASCENDING);

    public static Icon DECENDING = new SortArrowIcon(SortArrowIcon.DECENDING);

    public SortHeaderRenderer() {
        setHorizontalTextPosition(LEFT);
        setHorizontalAlignment(CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int col) {
        int index = -1;
        boolean ascending = true;
        if (table instanceof JSortTable) {
            JSortTable sortTable = (JSortTable) table;
            index = sortTable.getSortedColumnIndex();
            ascending = sortTable.isSortedColumnAscending();
        }
        if (table != null) {
            JTableHeader header = table.getTableHeader();
            if (header != null) {
                setForeground(header.getForeground());
                setBackground(header.getBackground());
                setFont(header.getFont());
            }
        }
        Icon icon = ascending ? ASCENDING : DECENDING;
        setIcon(col == index ? icon : NONSORTED);
        setText(value == null ? "" : value.toString());
        setBorder(UIManager.getBorder("TableHeader.cellBorder"));
        return this;
    }
}

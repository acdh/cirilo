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

import java.util.Collections;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

public class DefaultSortTableModel extends DefaultTableModel implements SortTableModel {

    private static final long serialVersionUID = 1L;
    private Vector names;
    
    public DefaultSortTableModel() {
    }

    public DefaultSortTableModel(int rows, int cols) {
        super(rows, cols);
    }

    public DefaultSortTableModel(Vector data, Vector names) {
        super(data, names);
     	setColumnIdentifiers(names);
        this.names = names;
    }

    public boolean isSortable(int col) {
        // return true; // FIXME: columns can't be sorted till the
        // how-do-i-get-the-pid-if-its-not-part-of-the-table-model-and-the-model-has-been-sorted
        // problem is solved
        return false;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void sortColumn(int col, boolean ascending) {
        Collections.sort(getDataVector(), new ColumnComparator(col, ascending));
    }
              
    public String getColumnName(int index) {
        return (String) names.get(index);
    }
    
    
}

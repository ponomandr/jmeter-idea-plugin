package idea.plugin.jmeter.run;

import com.intellij.openapi.actionSystem.ActionToolbarPosition;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.EditableModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PropertiesTable extends JPanel {
    private JBTable table;

    public PropertiesTable() {
        super(new BorderLayout());
        table = new JBTable(new MyDefaultTableModel());
        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(table).setToolbarPosition(ActionToolbarPosition.LEFT);
        add(decorator.createPanel(), BorderLayout.CENTER);
    }

    private static class MyDefaultTableModel extends DefaultTableModel implements EditableModel {
        public MyDefaultTableModel() {
            super(new String[]{"Property", "Value"}, 1);
        }

        @Override
        public void addRow() {
            addRow(new Object[]{"", ""});
        }

        @Override
        public void exchangeRows(int oldIndex, int newIndex) {
            moveRow(oldIndex, oldIndex, newIndex);
        }
    }
}

package idea.plugin.jmeter.run;

import com.intellij.openapi.actionSystem.ActionToolbarPosition;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.EditableModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.isBlank;

public class PropertyTable extends JPanel {
    private final PropertyTableModel model;

    public PropertyTable() {
        super(new BorderLayout());
        model = new PropertyTableModel();
        JBTable table = new JBTable(model);
        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(table).setToolbarPosition(ActionToolbarPosition.RIGHT);
        add(decorator.createPanel(), BorderLayout.CENTER);
    }

    public LinkedHashMap<String, String> getProperties() {
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        for (int row = 0; row < model.getRowCount(); row++) {
            Object key = model.getValueAt(row, 0);
            if (key == null || isBlank(key.toString())) continue;

            Object value = model.getValueAt(row, 1);
            map.put(key.toString(), value == null ? "" : value.toString().trim());
        }
        return map;
    }

    public void setProperties(Map<String, String> map) {
        model.setRowCount(map.size());
        int row = 0;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            model.setValueAt(entry.getKey(), row, 0);
            model.setValueAt(entry.getValue(), row, 1);
            row++;
        }
    }

    private static class PropertyTableModel extends DefaultTableModel implements EditableModel {
        public PropertyTableModel() {
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

        @Override
        public boolean canExchangeRows(int oldIndex, int newIndex) {
            return true;
        }
    }
}

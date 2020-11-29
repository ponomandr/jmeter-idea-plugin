package idea.plugin.jmeter.settings;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.*;

public class JmeterSettingsForm {
    private JPanel rootPanel;
    private TextFieldWithBrowseButton jmeterHome;
    private JCheckBox override;


    public JmeterSettingsForm(final Project project) {
        jmeterHome.addActionListener(e -> {
            VirtualFile file = FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFolderDescriptor(), project, null);
            if (file != null) {
                jmeterHome.setText(file.getPath());
            }
        });

        override.addChangeListener(e -> jmeterHome.setEnabled(override.isSelected()));
    }

    public void setData(JmeterSettings data) {
        jmeterHome.setText(data.getJmeterHome());
        override.setSelected(data.isOverride());
        if (!data.isOverride()) {
            jmeterHome.setText(System.getenv("JMETER_HOME"));
        }
    }

    public void getData(JmeterSettings data) {
        data.setJmeterHome(jmeterHome.getText().trim());
        data.setOverride(override.isSelected());
    }

    public boolean isModified(JmeterSettings data) {
        if (!jmeterHome.getText().equals(data.getJmeterHome())) return true;
        if (override.isSelected() != data.isOverride()) return true;
        return false;
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }
}

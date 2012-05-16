package idea.plugin.jmeter.run;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JmeterRunConfigurationForm {
    private JPanel rootPanel;
    private TextFieldWithBrowseButton testFile;
    private TextFieldWithBrowseButton propertyFile;
    private JCheckBox nongui;

    public JmeterRunConfigurationForm(final Project project) {
        testFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VirtualFile file = FileChooser.chooseFile(project, FileChooserDescriptorFactory.createSingleLocalFileDescriptor());
                if (file != null) {
                    testFile.setText(file.getPath());
                }
            }
        });

        propertyFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VirtualFile file = FileChooser.chooseFile(project, FileChooserDescriptorFactory.createSingleLocalFileDescriptor());
                if (file != null) {
                    propertyFile.setText(file.getPath());
                }
            }
        });
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public String getTestFile() {
        return testFile.getText();
    }

    public void setTestFile(String testFile) {
        this.testFile.setText(testFile);
    }

    public String getPropertyFile() {
        return propertyFile.getText();
    }

    public void setPropertyFile(String propertyFile) {
        this.propertyFile.setText(propertyFile);
    }

    public boolean isNongui() {
        return nongui.isSelected();
    }

    public void setNongui(boolean nongui) {
        this.nongui.setSelected(nongui);
    }
}

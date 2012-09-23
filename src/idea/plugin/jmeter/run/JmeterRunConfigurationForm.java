package idea.plugin.jmeter.run;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;

public class JmeterRunConfigurationForm {
    private JPanel rootPanel;
    private TextFieldWithBrowseButton testFile;
    private TextFieldWithBrowseButton propertyFile;
    private PropertyTable propertyTable;
    private JTextField jvmParameters;
    private JTextField customParameters;
    private TextFieldWithBrowseButton workingDirectory;

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

        workingDirectory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VirtualFile file = FileChooser.chooseFile(project, FileChooserDescriptorFactory.createSingleFolderDescriptor());
                if (file != null) {
                    workingDirectory.setText(file.getPath());
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

    public LinkedHashMap<String, String> getProperties() {
        return propertyTable.getProperties();
    }

    public void setProperties(Map<String, String> properties) {
        propertyTable.setProperties(properties);
    }

    public String getJvmParameters() {
        return jvmParameters.getText();
    }

    public void setJvmParameters(String jvmParameters) {
        this.jvmParameters.setText(jvmParameters);
    }

    public String getCustomParameters() {
        return customParameters.getText();
    }

    public void setCustomParameters(String customParameters) {
        this.customParameters.setText(customParameters);
    }

    public String getWorkingDirectory() {
        return workingDirectory.getText();
    }

    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory.setText(workingDirectory);
    }
}

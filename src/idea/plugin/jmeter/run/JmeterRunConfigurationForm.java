package idea.plugin.jmeter.run;

import javax.swing.*;

public class JmeterRunConfigurationForm {
    private JTextField testFile;
    private JPanel rootPanel;

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public String getTestFile() {
        return testFile.getText();
    }

    public void setTestFile(String testFile) {
        this.testFile.setText(testFile);
    }
}

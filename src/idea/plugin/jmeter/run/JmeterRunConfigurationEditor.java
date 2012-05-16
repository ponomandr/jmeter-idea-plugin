package idea.plugin.jmeter.run;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class JmeterRunConfigurationEditor extends SettingsEditor<JmeterRunConfiguration> {

    private final Project project;
    private JmeterRunConfigurationForm form;

    public JmeterRunConfigurationEditor(Project project) {
        this.project = project;
    }

    @Override
    protected void resetEditorFrom(JmeterRunConfiguration runConfiguration) {
        form.setTestFile(runConfiguration.getTestFile());
        form.setPropertyFile(runConfiguration.getPropertyFile());
        form.setNongui(runConfiguration.isNongui());
        form.setProperties(runConfiguration.getProperties());
    }

    @Override
    protected void applyEditorTo(JmeterRunConfiguration runConfiguration) throws ConfigurationException {
        runConfiguration.setTestFile(form.getTestFile());
        runConfiguration.setPropertyFile(form.getPropertyFile());
        runConfiguration.setNongui(form.isNongui());
        runConfiguration.setProperties(form.getProperties());
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        form = new JmeterRunConfigurationForm(project);
        return form.getRootPanel();
    }

    @Override
    protected void disposeEditor() {
        form = null;
    }

}

package idea.plugin.jmeter.run;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class JmeterRunConfigurationEditor extends SettingsEditor<JmeterRunConfiguration> {

    private JmeterRunConfigurationForm form;
    private Project project;

    public JmeterRunConfigurationEditor(Project project) {
        this.project = project;
    }

    @Override
    protected void resetEditorFrom(JmeterRunConfiguration jmeterRunConfiguration) {
        form.setTestFile(jmeterRunConfiguration.getTestFile());
        form.setPropertyFile(jmeterRunConfiguration.getPropertyFile());
        form.setNongui(jmeterRunConfiguration.isNongui());
    }

    @Override
    protected void applyEditorTo(JmeterRunConfiguration jmeterRunConfiguration) throws ConfigurationException {
        jmeterRunConfiguration.setTestFile(form.getTestFile());
        jmeterRunConfiguration.setPropertyFile(form.getPropertyFile());
        jmeterRunConfiguration.setNongui(form.isNongui());
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

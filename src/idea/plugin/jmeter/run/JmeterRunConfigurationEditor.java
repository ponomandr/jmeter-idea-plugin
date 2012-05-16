package idea.plugin.jmeter.run;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class JmeterRunConfigurationEditor extends SettingsEditor<JmeterRunConfiguration> {

    private JmeterRunConfigurationForm form;

    @Override
    protected void resetEditorFrom(JmeterRunConfiguration jmeterRunConfiguration) {
        form.setTestFile(jmeterRunConfiguration.getTestFile());
    }

    @Override
    protected void applyEditorTo(JmeterRunConfiguration jmeterRunConfiguration) throws ConfigurationException {
        jmeterRunConfiguration.setTestFile(form.getTestFile());
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        form = new JmeterRunConfigurationForm();
        return form.getRootPanel();
    }

    @Override
    protected void disposeEditor() {
        form = null;
    }

}

package idea.plugin.jmeter.run;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class JmeterRunConfigurationEditor extends SettingsEditor<JmeterRunConfiguration> {

    @Override
    protected void resetEditorFrom(JmeterRunConfiguration jmeterRunConfiguration) {
    }

    @Override
    protected void applyEditorTo(JmeterRunConfiguration jmeterRunConfiguration) throws ConfigurationException {
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return new JLabel("JmeterRunConfigurationEditor");
    }

    @Override
    protected void disposeEditor() {
    }
}

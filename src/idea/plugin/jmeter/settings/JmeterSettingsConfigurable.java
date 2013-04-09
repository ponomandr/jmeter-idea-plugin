package idea.plugin.jmeter.settings;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class JmeterSettingsConfigurable implements SearchableConfigurable {

    private final Project project;
    private final PropertiesComponent propertiesComponent;
    private JmeterSettingsForm settingsForm;
    private JmeterSettings settings;

    public JmeterSettingsConfigurable(Project project) {
        this.project = project;
        propertiesComponent = PropertiesComponent.getInstance(project);
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "JMeter";
    }

    @Override
    public String getHelpTopic() {
        return null;
    }

    @Override
    public JComponent createComponent() {
        settings = JmeterSettings.read(propertiesComponent);
        settingsForm = new JmeterSettingsForm(project);
        return settingsForm.getRootPanel();
    }

    @Override
    public boolean isModified() {
        return settingsForm.isModified(settings);
    }

    @Override
    public void apply() throws ConfigurationException {
        settingsForm.getData(settings);
        settings.save(propertiesComponent);
    }

    @Override
    public void reset() {
        settingsForm.setData(settings);
    }

    @Override
    public void disposeUIResources() {
        settingsForm = null;
    }

    @NotNull
    @Override
    public String getId() {
        return getClass().getName();
    }

    @Override
    public Runnable enableSearch(String s) {
        return null;
    }
}

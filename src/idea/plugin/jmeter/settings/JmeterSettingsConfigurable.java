package idea.plugin.jmeter.settings;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class JmeterSettingsConfigurable implements SearchableConfigurable {

    private JmeterSettingsPresenter settingsPresenter;
    private final Project project;

    public JmeterSettingsConfigurable(Project project) {
        this.project = project;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "JMeter";
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @Override
    public String getHelpTopic() {
        return null;
    }

    @Override
    public JComponent createComponent() {
        JmeterSettingsView settingsView = new JmeterSettingsView(project);
        settingsPresenter = new JmeterSettingsPresenter(PropertiesComponent.getInstance(project), settingsView);
        return settingsView;
    }

    @Override
    public boolean isModified() {
        return settingsPresenter.isModified();
    }

    @Override
    public void apply() throws ConfigurationException {
        settingsPresenter.save();
    }

    @Override
    public void reset() {
        settingsPresenter.load();
    }

    @Override
    public void disposeUIResources() {
        settingsPresenter = null;
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

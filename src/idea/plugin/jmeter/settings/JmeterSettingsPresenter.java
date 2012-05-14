package idea.plugin.jmeter.settings;

import com.intellij.ide.util.PropertiesComponent;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class JmeterSettingsPresenter implements ChangeListener {
    public static final String JMETER_HOME_KEY = "jmeter.home";

    private final PropertiesComponent propertiesComponent;
    private final JmeterSettingsView view;
    private boolean modified;

    public JmeterSettingsPresenter(PropertiesComponent propertiesComponent, JmeterSettingsView view) {
        this.propertiesComponent = propertiesComponent;
        this.view = view;
        view.setPresenter(this);
    }

    public boolean isModified() {
        return modified;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        modified = true;
    }

    public void save() {
        propertiesComponent.setValue(JMETER_HOME_KEY, view.getJmeterHome().trim());
    }

    public void load() {
        view.setJmeterHome(propertiesComponent.getValue(JMETER_HOME_KEY));
    }
}

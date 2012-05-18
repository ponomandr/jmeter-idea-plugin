package idea.plugin.jmeter.settings;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;

import java.io.File;

public class JmeterSettings {
    private static final String OVERRIDE_KEY = "jmeter.override";
    private static final String JMETER_HOME_KEY = "jmeter.home";

    private String jmeterHome;
    private boolean override;

    public static File getJmeterJar(Project project) {
        File binDir = new File(getJmeterHome(project), "bin");
        return new File(binDir, "ApacheJMeter.jar");
    }

    public String getJmeterHome() {
        return jmeterHome;
    }

    public void setJmeterHome(String jmeterHome) {
        this.jmeterHome = jmeterHome;
    }

    public boolean isOverride() {
        return override;
    }

    public void setOverride(boolean override) {
        this.override = override;
    }

    public static String getJmeterHome(Project project) {
        JmeterSettings settings = read(PropertiesComponent.getInstance(project));
        return settings.isOverride() ? settings.getJmeterHome() : System.getenv("JMETER_HOME");
    }

    public static JmeterSettings read(PropertiesComponent properties) {
        JmeterSettings settings = new JmeterSettings();
        settings.setJmeterHome(properties.getValue(JMETER_HOME_KEY));
        settings.setOverride(properties.getBoolean(OVERRIDE_KEY, false));
        return settings;
    }

    public void save(PropertiesComponent properties) {
        properties.setValue(JMETER_HOME_KEY, jmeterHome);
        properties.setValue(OVERRIDE_KEY, String.valueOf(override));
    }

}
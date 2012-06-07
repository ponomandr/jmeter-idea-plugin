package idea.plugin.jmeter.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.JDOMExternalizer;
import com.intellij.openapi.util.WriteExternalException;
import idea.plugin.jmeter.settings.JmeterSettings;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.LinkedHashMap;

import static org.apache.commons.lang.StringUtils.isBlank;

public class JmeterRunConfiguration extends RunConfigurationBase implements LocatableConfiguration {
    private String testFile;
    private String propertyFile;
    private LinkedHashMap<String, String> properties = new LinkedHashMap<String, String>();
    private String customParameters;
    private String workingDirectory;

    public JmeterRunConfiguration(Project project, ConfigurationFactory configurationFactory) {
        super(project, configurationFactory, "");
    }

    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new JmeterRunConfigurationEditor(getProject());
    }

    @Override
    public JDOMExternalizable createRunnerSettings(ConfigurationInfoProvider configurationInfoProvider) {
        return null;
    }

    @Override
    public SettingsEditor<JDOMExternalizable> getRunnerSettingsEditor(ProgramRunner programRunner) {
        return null;
    }

    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment executionEnvironment) throws ExecutionException {
        return new JmeterRunProfileState(executionEnvironment);
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        if (testFile == null || !new File(testFile).exists()) {
            throw new RuntimeConfigurationException("Test file not found");
        }

        if (!isBlank(propertyFile) && !new File(propertyFile).exists()) {
            throw new RuntimeConfigurationException("Properties file not found");
        }

        if (!JmeterSettings.getJmeterJar(getProject()).exists()) {
            throw new RuntimeConfigurationException("JMeter not found");
        }
    }

    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        super.writeExternal(element);
        JDOMExternalizer.write(element, "testFile", testFile);
        JDOMExternalizer.write(element, "propertyFile", propertyFile);
        JDOMExternalizer.write(element, "customParameters", customParameters);
        JDOMExternalizer.writeMap(element, properties, "properties", "property");
    }

    @Override
    public void readExternal(Element element) throws InvalidDataException {
        super.readExternal(element);
        testFile = JDOMExternalizer.readString(element, "testFile");
        propertyFile = JDOMExternalizer.readString(element, "propertyFile");
        customParameters = JDOMExternalizer.readString(element, "customParameters");

        LinkedHashMap<String, String> properties = this.properties;
        JDOMExternalizer.readMap(element, properties, "properties", "property");
        this.properties = properties;
    }

    public String getTestFile() {
        return testFile;
    }

    public void setTestFile(String testFile) {
        this.testFile = testFile;
    }

    public String getPropertyFile() {
        return propertyFile;
    }

    public void setPropertyFile(String propertyFile) {
        this.propertyFile = propertyFile;
    }

    public LinkedHashMap<String, String> getProperties() {
        return properties;
    }

    public void setProperties(LinkedHashMap<String, String> properties) {
        this.properties = properties;
    }

    public String getCustomParameters() {
        return customParameters;
    }

    public void setCustomParameters(String customParameters) {
        this.customParameters = customParameters;
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    @Override
    public boolean isGeneratedName() {
        return false;
    }

    @Override
    public String suggestedName() {
        return getName();
    }
}

package idea.plugin.jmeter.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.filters.TextConsoleBuilderImpl;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.impl.JavaAwareProjectJdkTableImpl;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.JDOMExternalizer;
import com.intellij.openapi.util.WriteExternalException;
import idea.plugin.jmeter.settings.JmeterSettings;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.isBlank;

public class JmeterRunConfiguration extends RunConfigurationBase implements RunConfiguration {
    private String testFile;
    private String propertyFile;
    private boolean nongui;
    private LinkedHashMap<String, String> properties = new LinkedHashMap<String, String>();
    private String customParameters;

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
        JDOMExternalizer.write(element, "nongui", nongui);
        JDOMExternalizer.write(element, "customParameters", customParameters);
        JDOMExternalizer.writeMap(element, properties, "properties", "property");
    }

    @Override
    public void readExternal(Element element) throws InvalidDataException {
        super.readExternal(element);
        testFile = JDOMExternalizer.readString(element, "testFile");
        propertyFile = JDOMExternalizer.readString(element, "propertyFile");
        nongui = JDOMExternalizer.readBoolean(element, "propertyFile");
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

    public boolean isNongui() {
        return nongui;
    }

    public void setNongui(boolean nongui) {
        this.nongui = nongui;
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

    private class JmeterRunProfileState extends JavaCommandLineState {
        public JmeterRunProfileState(ExecutionEnvironment executionEnvironment) {
            super(executionEnvironment);
            setConsoleBuilder(new TextConsoleBuilderImpl(getProject()));
        }

        @Override
        protected JavaParameters createJavaParameters() throws ExecutionException {
            JavaParameters parameters = new JavaParameters();
            parameters.setJdk(JavaAwareProjectJdkTableImpl.getInstanceEx().getInternalJdk());
            parameters.setMainClass("org.apache.jmeter.NewDriver");
            parameters.getClassPath().add(JmeterSettings.getJmeterJar(getProject()));

            ParametersList programParameters = parameters.getProgramParametersList();
            programParameters.add("-t", testFile);
            if (nongui) {
                programParameters.add("-n");
            }
            if (!isBlank(propertyFile)) {
                programParameters.add("-p", propertyFile);
            }
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                programParameters.add("-J", entry.getKey() + "=" + entry.getValue());
            }
            programParameters.addParametersString(customParameters);
            return parameters;
        }
    }
}

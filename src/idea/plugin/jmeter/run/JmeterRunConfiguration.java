package idea.plugin.jmeter.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.impl.JavaAwareProjectJdkTableImpl;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.JDOMExternalizerUtil;
import com.intellij.openapi.util.WriteExternalException;
import idea.plugin.jmeter.settings.JmeterSettings;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class JmeterRunConfiguration extends RunConfigurationBase implements RunConfiguration {
    private String testFile;

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
        if (!new File(testFile).exists()) {
            throw new RuntimeConfigurationException("Test file not found");
        }

        if (!JmeterSettings.getJmeterJar(getProject()).exists()) {
            throw new RuntimeConfigurationException("JMeter not found");
        }
    }

    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        super.writeExternal(element);
        JDOMExternalizerUtil.writeField(element, "testFile", testFile);
    }

    @Override
    public void readExternal(Element element) throws InvalidDataException {
        super.readExternal(element);
        testFile = JDOMExternalizerUtil.readField(element, "testFile");
    }

    public String getTestFile() {
        return testFile;
    }

    public void setTestFile(String testFile) {
        this.testFile = testFile;
    }

    private class JmeterRunProfileState extends JavaCommandLineState {
        public JmeterRunProfileState(ExecutionEnvironment executionEnvironment) {
            super(executionEnvironment);
        }

        @Override
        protected JavaParameters createJavaParameters() throws ExecutionException {
            JavaParameters parameters = new JavaParameters();
            parameters.setJdk(JavaAwareProjectJdkTableImpl.getInstanceEx().getInternalJdk());
            parameters.setMainClass("org.apache.jmeter.NewDriver");
            parameters.getClassPath().add(JmeterSettings.getJmeterJar(getProject()));
            ParametersList programParameters = parameters.getProgramParametersList();
            programParameters.add("-t", testFile);
            return parameters;
        }
    }
}

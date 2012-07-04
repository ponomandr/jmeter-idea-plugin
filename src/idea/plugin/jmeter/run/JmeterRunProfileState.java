package idea.plugin.jmeter.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.JavaCommandLineState;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.projectRoots.impl.JavaAwareProjectJdkTableImpl;
import idea.plugin.jmeter.settings.JmeterSettings;

import java.util.Map;

import static org.apache.commons.lang.StringUtils.isBlank;

class JmeterRunProfileState extends JavaCommandLineState {
    private final JmeterRunConfiguration runConfiguration;
    private final ExecutionEnvironment executionEnvironment;

    public JmeterRunProfileState(ExecutionEnvironment executionEnvironment) {
        super(executionEnvironment);
        this.runConfiguration = (JmeterRunConfiguration) executionEnvironment.getRunProfile();
        this.executionEnvironment = executionEnvironment;
        setConsoleBuilder(TextConsoleBuilderFactory.getInstance().createBuilder(executionEnvironment.getProject()));
    }

    @Override
    protected JavaParameters createJavaParameters() throws ExecutionException {
        JavaParameters parameters = new JavaParameters();
        parameters.setJdk(JavaAwareProjectJdkTableImpl.getInstanceEx().getInternalJdk());
        parameters.setMainClass("org.apache.jmeter.NewDriver");
        parameters.getClassPath().add(JmeterSettings.getJmeterJar(executionEnvironment.getProject()));
        if (!isBlank(runConfiguration.getWorkingDirectory())) {
            parameters.setWorkingDirectory(runConfiguration.getWorkingDirectory());
        }

        ParametersList programParameters = parameters.getProgramParametersList();
        programParameters.add("--testfile", runConfiguration.getTestFile());

        if (!isBlank(runConfiguration.getPropertyFile())) {
            programParameters.add("--addprop", runConfiguration.getPropertyFile());
        }

        for (Map.Entry<String, String> entry : runConfiguration.getProperties().entrySet()) {
            programParameters.add("-J", entry.getKey() + "=" + entry.getValue());
        }
        programParameters.addParametersString(runConfiguration.getCustomParameters());

        if (runConfiguration.isNonguiMode()) {
            programParameters.add("--nongui");
        }

        return parameters;
    }
}

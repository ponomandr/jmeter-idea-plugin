package idea.plugin.jmeter.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.JavaCommandLineState;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.filters.TextConsoleBuilderImpl;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.projectRoots.impl.JavaAwareProjectJdkTableImpl;
import idea.plugin.jmeter.settings.JmeterSettings;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.isBlank;

class JmeterRunProfileState extends JavaCommandLineState {
    private final JmeterRunConfiguration runConfiguration;
    private final ExecutionEnvironment executionEnvironment;
    private final File logFile;

    public JmeterRunProfileState(ExecutionEnvironment executionEnvironment) throws IOException {
        super(executionEnvironment);
        this.runConfiguration = (JmeterRunConfiguration) executionEnvironment.getRunProfile();
        this.executionEnvironment = executionEnvironment;
        logFile = File.createTempFile("jmeter", ".jtl");

        setConsoleBuilder(new TextConsoleBuilderImpl(executionEnvironment.getProject()) {
            @Override
            protected ConsoleView createConsole() {
                if (runConfiguration.isNonguiMode()) {
                    return new JmeterConsoleView(logFile);
                } else {
                    return new ConsoleViewImpl(getProject(), true);
                }
            }
        });
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
            programParameters.add("--logfile", logFile.getAbsolutePath());
            programParameters.add("--nongui");
            programParameters.add("-J", "jmeter.save.saveservice.assertion_results_failure_message=true");
            programParameters.add("-J", "jmeter.save.saveservice.assertion_results=true");
            programParameters.add("-J", "jmeter.save.saveservice.assertions=true");
//            programParameters.add("-J", "jmeter.save.saveservice.bytes=true");
//            programParameters.add("-J", "jmeter.save.saveservice.data_type=true");
//            programParameters.add("-J", "jmeter.save.saveservice.default_delimiter=true");
//            programParameters.add("-J", "jmeter.save.saveservice.encoding=true");
//            programParameters.add("-J", "jmeter.save.saveservice.filename=true");
//            programParameters.add("-J", "jmeter.save.saveservice.hostname=true");
//            programParameters.add("-J", "jmeter.save.saveservice.idle_time=true");
            programParameters.add("-J", "jmeter.save.saveservice.label=true");
//            programParameters.add("-J", "jmeter.save.saveservice.latency=true");
//            programParameters.add("-J", "jmeter.save.saveservice.output_format=true");
//            programParameters.add("-J", "jmeter.save.saveservice.print_field_names=true");
            programParameters.add("-J", "jmeter.save.saveservice.requestHeaders=true");
            programParameters.add("-J", "jmeter.save.saveservice.response_code=true");
//            programParameters.add("-J", "jmeter.save.saveservice.response_data.on_error=true");
            programParameters.add("-J", "jmeter.save.saveservice.response_data=true");
            programParameters.add("-J", "jmeter.save.saveservice.responseHeaders=true");
            programParameters.add("-J", "jmeter.save.saveservice.response_message=true");
//            programParameters.add("-J", "jmeter.save.saveservice.sample_count=true");
            programParameters.add("-J", "jmeter.save.saveservice.samplerData=true");
            programParameters.add("-J", "jmeter.save.saveservice.subresults=true");
//            programParameters.add("-J", "jmeter.save.saveservice.successful=true");
//            programParameters.add("-J", "jmeter.save.saveservice.thread_counts=true");
            programParameters.add("-J", "jmeter.save.saveservice.thread_name=true");
//            programParameters.add("-J", "jmeter.save.saveservice.timestamp_format=true");
//            programParameters.add("-J", "jmeter.save.saveservice.time=true");
            programParameters.add("-J", "jmeter.save.saveservice.url=true");
//            programParameters.add("-J", "jmeter.save.saveservice.xml_pi=true");
        }

        return parameters;
    }

}

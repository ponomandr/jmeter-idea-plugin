package idea.plugin.jmeter.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.JavaCommandLineState;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.projectRoots.impl.JavaAwareProjectJdkTableImpl;
import idea.plugin.jmeter.run.tailer.Tailer;
import idea.plugin.jmeter.run.tailer.TailerListenerAdapter;
import idea.plugin.jmeter.settings.JmeterSettings;
import org.jetbrains.annotations.NotNull;

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
        setConsoleBuilder(TextConsoleBuilderFactory.getInstance().createBuilder(executionEnvironment.getProject()));
    }

    @Override
    protected ConsoleView createConsole(@NotNull Executor executor) throws ExecutionException {
        final ConsoleView console = super.createConsole(executor);
        if (console != null) {
            Tailer.create(logFile, new TailerListenerAdapter() {
                @Override
                public void handle(String line) {
                    console.print(line + '\n', ConsoleViewContentType.NORMAL_OUTPUT);
                }

                @Override
                public void handle(Exception ex) {
                    console.print(ex.toString(), ConsoleViewContentType.ERROR_OUTPUT);
                }
            });

        }
        return console;
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
        }

        return parameters;
    }
}

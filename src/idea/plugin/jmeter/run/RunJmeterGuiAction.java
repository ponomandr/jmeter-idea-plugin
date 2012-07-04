package idea.plugin.jmeter.run;

import com.intellij.execution.*;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.impl.RunManagerImpl;
import com.intellij.execution.impl.RunnerAndConfigurationSettingsImpl;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import idea.plugin.jmeter.JmeterFileType;

class RunJmeterGuiAction extends AnAction implements DumbAware {

    private final JmeterRunConfiguration runConfiguration;

    public RunJmeterGuiAction(JmeterRunConfiguration runConfiguration) {
        super("Run JMeter GUI", null, JmeterFileType.FILE_ICON);

        this.runConfiguration = runConfiguration;
    }

    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);

        Executor myExecutor = DefaultRunExecutor.getRunExecutorInstance();
        ProgramRunner runner = RunnerRegistry.getInstance().getRunner(myExecutor.getId(), runConfiguration);
        if (runner == null) {
            return;
        }

        if (ExecutorRegistry.getInstance().isStarting(project, myExecutor.getId(), runner.getRunnerId())) {
            return;
        }

        JmeterRunConfiguration guiRunConfiguration = runConfiguration.clone();
        guiRunConfiguration.setNonguiMode(false);

        RunManagerImpl runManager = (RunManagerImpl) RunManager.getInstance(project);
        RunnerAndConfigurationSettings runnerAndConfiguration = new RunnerAndConfigurationSettingsImpl(runManager, guiRunConfiguration, false);
        ExecutionEnvironment myEnvironment = new ExecutionEnvironment(runner, runnerAndConfiguration, project);
        try {
            RunProfileState state = myEnvironment.getState(myExecutor);
            if (state instanceof CommandLineState) {
                ((JmeterRunProfileState) state).setConsoleBuilder(null);
                runner.execute(myExecutor, myEnvironment);
            }
        } catch (RunCanceledByUserException ignore) {
        } catch (ExecutionException e1) {
            Messages.showErrorDialog(project, e1.getMessage(), ExecutionBundle.message("restart.error.message.title"));
        }
    }


/*
    @Override
    public void actionPerformed(AnActionEvent e) {
        ProjectManager projectManager = ProjectManager.getInstance();
        Project[] openProjects = projectManager.getOpenProjects();
        if (openProjects.length==0) {return;}
        Project project = openProjects[0];
        RunManager runManager = RunManager.getInstance(project);
        if (!( runManager.getSelectedConfiguration().getConfiguration() instanceof ApplicationConfiguration)) {
            return;
        }
        Executor executor = DefaultRunExecutor.getRunExecutorInstance();
        RunnerAndConfigurationSettingsImpl selectedConfiguration = new RunnerAndConfigurationSettingsImpl(
                (RunManagerImpl) runManager,
                new ApplicationConfigurationWrapper((ApplicationConfiguration) runManager.getSelectedConfiguration().getConfiguration()),
                runManager.getSelectedConfiguration().isTemplate()
        );
        ProgramRunner runner = RunnerRegistry.getInstance().getRunner(executor.getId(),selectedConfiguration.getConfiguration());
        ExecutionEnvironment environment = new ExecutionEnvironment(runner, selectedConfiguration, e.getDataContext());
        try {
            runner.execute(executor, environment);
        } catch (ExecutionException e1) {
            JavaExecutionUtil.showExecutionErrorMessage(e1, "Error", project);
        }
    }
*/
}

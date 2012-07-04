package idea.plugin.jmeter.run;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import idea.plugin.jmeter.JmeterFileType;

class RunJmeterGuiAction extends AnAction {
    public RunJmeterGuiAction(JmeterRunConfiguration runConfiguration) {
        super("Run JMeter GUI", null, JmeterFileType.FILE_ICON);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
/*
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
*/
    }
}

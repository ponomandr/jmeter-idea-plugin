package idea.plugin.jmeter;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.RunManagerEx;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.RunnerRegistry;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import idea.plugin.jmeter.run.JmeterConfigurationType;
import idea.plugin.jmeter.run.JmeterRunConfiguration;

import static idea.plugin.jmeter.settings.JmeterSettings.getJmeterJar;


public class RunJmeterAction extends AnAction {

    public RunJmeterAction() {
        super("R&un", null, IconLoader.getIcon("/general/toolWindowRun.png"));
    }

    @Override
    public void update(AnActionEvent event) {
        super.update(event);
        VirtualFile file = event.getData(PlatformDataKeys.VIRTUAL_FILE);
        Project project = event.getData(PlatformDataKeys.PROJECT);
        event.getPresentation().setVisible(isJmeterFile(file));
        event.getPresentation().setEnabled(getJmeterJar(project).exists());
        event.getPresentation().setText(getText(file), true);
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        VirtualFile file = event.getData(PlatformDataKeys.VIRTUAL_FILE);
        if (file != null) {
            Project project = event.getData(PlatformDataKeys.PROJECT);
            openWithJMeter(file, project);
        }
    }

    private boolean isJmeterFile(VirtualFile file) {
        return file != null && JmeterFileType.INSTANCE.equals(file.getFileType());
    }

    private String getText(VirtualFile file) {
        if (file == null) {
            return getTemplatePresentation().getTextWithMnemonic();
        } else {
            return getTemplatePresentation().getTextWithMnemonic() + " '" + StringUtil.first(file.getNameWithoutExtension(), 30, true) + "'";
        }
    }

    private void openWithJMeter(VirtualFile file, Project project) {
        try {
            JmeterConfigurationType configurationType = ConfigurationTypeUtil.findConfigurationType(JmeterConfigurationType.class);
            RunnerAndConfigurationSettings configurationSettings = RunManagerEx.getInstanceEx(project).createConfiguration(file.getNameWithoutExtension(), configurationType.getConfigurationFactory());
            JmeterRunConfiguration runConfiguration = (JmeterRunConfiguration) configurationSettings.getConfiguration();
            runConfiguration.setTestFile(file.getPath());

            RunManagerEx.getInstanceEx(project).setTemporaryConfiguration(configurationSettings);

            ProgramRunner runner = RunnerRegistry.getInstance().getRunner(DefaultRunExecutor.EXECUTOR_ID, runConfiguration);
            assert runner != null;
            ExecutionEnvironment executionEnvironment = new ExecutionEnvironment(runConfiguration, project, null, null, null);
            runner.execute(DefaultRunExecutor.getRunExecutorInstance(), executionEnvironment);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}

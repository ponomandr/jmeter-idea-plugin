package idea.plugin.jmeter;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;
import java.io.IOException;

import static idea.plugin.jmeter.settings.JmeterSettingsPresenter.JMETER_HOME_KEY;
import static org.apache.commons.lang.StringUtils.isBlank;

public class OpenWithJmeterAction extends AnAction {

    private static final boolean IS_OS_WINDOWS = System.getProperty("os.name").startsWith("Windows");

    public OpenWithJmeterAction() {
        super("Open with JMeter");
    }

    @Override
    public void update(AnActionEvent event) {
        super.update(event);
        VirtualFile file = event.getData(PlatformDataKeys.VIRTUAL_FILE);
        Project project = event.getData(PlatformDataKeys.PROJECT);
        event.getPresentation().setVisible(isJmeterFile(file));
        event.getPresentation().setEnabled(getJmeterExecutable(project).exists());
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

    private void openWithJMeter(VirtualFile file, Project project) {
        try {
            String command = getJmeterExecutable(project).getPath() + " -t " + file.getPath();
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static File getJmeterExecutable(Project project) {
        String jmeterHome = getJmeterHome(project);
        String ext = IS_OS_WINDOWS ? ".bat" : ".sh";
        return new File(jmeterHome, "bin/jmeter" + ext);
    }

    private static String getJmeterHome(Project project) {
        String jmeterHome = PropertiesComponent.getInstance(project).getValue(JMETER_HOME_KEY);
        return isBlank(jmeterHome) ? System.getenv("JMETER_HOME") : jmeterHome;
    }

}

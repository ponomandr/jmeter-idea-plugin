package idea.plugin.jmeter;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import idea.plugin.jmeter.settings.JmeterSettings;

import java.io.File;
import java.io.IOException;


public class OpenWithJmeterAction extends AnAction {

    private static final boolean IS_OS_WINDOWS = System.getProperty("os.name").startsWith("Windows");

    public OpenWithJmeterAction() {
        super("Open with JMeter", null, JmeterFileType.FILE_ICON);
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
        String jmeterHome = JmeterSettings.getJmeterHome(project);
        String ext = IS_OS_WINDOWS ? ".bat" : ".sh";
        return new File(jmeterHome, "bin/jmeter" + ext);
    }

}

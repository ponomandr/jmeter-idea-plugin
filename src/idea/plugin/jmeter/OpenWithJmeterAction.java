package idea.plugin.jmeter;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;
import java.io.IOException;

public class OpenWithJmeterAction extends AnAction {

    private static final boolean IS_OS_WINDOWS = System.getProperty("os.name").startsWith("Windows");

    private final File jmeter = getJmeterExecutable();


    public OpenWithJmeterAction() {
        super("Open with JMeter");
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);
        VirtualFile file = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        e.getPresentation().setVisible(isJmeterFile(file));
        e.getPresentation().setEnabled(jmeter.exists());
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        VirtualFile file = event.getData(PlatformDataKeys.VIRTUAL_FILE);
        if (file != null) {
            openWithJMeter(file);
        }
    }

    private static File getJmeterExecutable() {
        String jmeterHome = System.getenv("JMETER_HOME");
        String ext = IS_OS_WINDOWS ? ".bat" : ".sh";
        return new File(jmeterHome, "bin/jmeter" + ext);
    }

    private boolean isJmeterFile(VirtualFile file) {
        return file != null && JmeterFileType.INSTANCE.equals(file.getFileType());
    }

    private void openWithJMeter(VirtualFile file) {
        try {
            String command = jmeter.getPath() + " -t " + file.getPath();
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

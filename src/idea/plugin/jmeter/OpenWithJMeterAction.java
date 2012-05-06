package idea.plugin.jmeter;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;
import java.io.IOException;

public class OpenWithJMeterAction extends AnAction {

    private static final boolean IS_OS_WINDOWS = System.getProperty("os.name").startsWith("Windows");

    private final File jmeter;


    public OpenWithJMeterAction() {
        super("Open with JMeter");

        String jmeterHome = System.getenv("JMETER_HOME");
        String ext = IS_OS_WINDOWS ? ".bat" : ".sh";
        jmeter = new File(jmeterHome, "bin/jmeter" + ext);
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        VirtualFile file = event.getData(PlatformDataKeys.VIRTUAL_FILE);
        if (file == null) {
            return;
        }

        try {
            String command = jmeter.getPath() + " -t " + file.getPath();
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);
        VirtualFile file = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        if (file != null) {
            e.getPresentation().setVisible(JMeterFileType.INSTANCE.equals(file.getFileType()));
        }
        e.getPresentation().setEnabled(jmeter.exists());
    }

}

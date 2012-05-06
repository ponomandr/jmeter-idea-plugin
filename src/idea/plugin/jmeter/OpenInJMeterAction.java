package idea.plugin.jmeter;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;
import java.io.IOException;

public class OpenInJMeterAction extends AnAction {
    private String jmeterHome = System.getenv("JMETER_HOME");

    public OpenInJMeterAction() {
        super("Open with JMeter");
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        VirtualFile file = event.getData(PlatformDataKeys.VIRTUAL_FILE);
        if (file == null) {
            return;
        }

        try {
            File jmeter = new File(jmeterHome, "bin/jmeter");
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
        e.getPresentation().setEnabled(jmeterHome != null);
    }
}

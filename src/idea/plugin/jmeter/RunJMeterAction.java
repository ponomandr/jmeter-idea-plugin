package idea.plugin.jmeter;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.vfs.VirtualFile;

public class RunJMeterAction extends AnAction {

    public RunJMeterAction() {
        super("Do nothing with JMeter");
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);
        VirtualFile file = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        if (file != null) {
            e.getPresentation().setVisible(JMeterFileType.INSTANCE.equals(file.getFileType()));
        }
    }
}

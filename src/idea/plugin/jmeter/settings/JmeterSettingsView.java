package idea.plugin.jmeter.settings;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.DocumentAdapter;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JmeterSettingsView extends JPanel {
    private final TextFieldWithBrowseButton jmeterHome = new TextFieldWithBrowseButton();

    public JmeterSettingsView(final Project project) {
        super(new BorderLayout());

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(new JLabel("JMeter home directory"), BorderLayout.WEST);
        panel.add(jmeterHome, BorderLayout.CENTER);
        add(panel, BorderLayout.NORTH);

        jmeterHome.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VirtualFile file = FileChooser.chooseFile(project, FileChooserDescriptorFactory.createSingleFolderDescriptor());
                if (file != null) {
                    jmeterHome.setText(file.getPath());
                }
            }
        });
    }

    public void setPresenter(final JmeterSettingsPresenter presenter) {
        jmeterHome.getTextField().getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(DocumentEvent documentEvent) {
                presenter.stateChanged(new ChangeEvent(JmeterSettingsView.this));
            }
        });
    }

    public String getJmeterHome() {
        return jmeterHome.getText();
    }

    public void setJmeterHome(String jmeterHome) {
        this.jmeterHome.setText(jmeterHome);
    }
}

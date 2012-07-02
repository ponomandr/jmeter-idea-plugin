package idea.plugin.jmeter;

import com.intellij.execution.Location;
import com.intellij.execution.RunManagerEx;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.junit.RuntimeConfigurationProducer;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import idea.plugin.jmeter.run.JmeterConfigurationType;
import idea.plugin.jmeter.run.JmeterRunConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class JmeterConfigurationProducer extends RuntimeConfigurationProducer {
    private PsiElement myPsiElement;

    public JmeterConfigurationProducer() {
        super(JmeterConfigurationType.getInstance());
    }

    @Override
    public PsiElement getSourceElement() {
        return myPsiElement;
    }

    @Override
    protected RunnerAndConfigurationSettings createConfigurationByElement(Location location, ConfigurationContext context) {
        myPsiElement = location.getPsiElement();

        VirtualFile testFile = location.getVirtualFile();
        if (testFile == null || !JmeterFileType.INSTANCE.equals(testFile.getFileType())) {
            return null;
        }

        Project project = location.getProject();

        ConfigurationFactory configurationFactory = JmeterConfigurationType.getInstance().getConfigurationFactory();
        RunnerAndConfigurationSettings configurationSettings = RunManagerEx.getInstanceEx(project).createConfiguration(testFile.getNameWithoutExtension(), configurationFactory);
        JmeterRunConfiguration runConfiguration = (JmeterRunConfiguration) configurationSettings.getConfiguration();
        runConfiguration.setTestFile(testFile.getPath());
        VirtualFile propertyFile = testFile.getParent().findChild(testFile.getNameWithoutExtension() + ".properties");
        if (propertyFile != null) {
            runConfiguration.setPropertyFile(propertyFile.getPath());
        }
        return configurationSettings;
    }

    @Override
    protected RunnerAndConfigurationSettings findExistingByElement(Location location, @NotNull RunnerAndConfigurationSettings[] existingConfigurations, ConfigurationContext context) {
        VirtualFile file = location.getVirtualFile();
        if (file == null) {
            return null;
        }

        for (RunnerAndConfigurationSettings existingConfiguration : existingConfigurations) {
            RunConfiguration runConfiguration = existingConfiguration.getConfiguration();
            if (runConfiguration instanceof JmeterRunConfiguration) {
                JmeterRunConfiguration jmeterRunConfiguration = (JmeterRunConfiguration) runConfiguration;
                String absolutePath1 = new File(jmeterRunConfiguration.getTestFile()).getAbsolutePath();
                String absolutePath2 = new File(file.getPath()).getAbsolutePath();

                if (absolutePath1.equals(absolutePath2)) {
                    return existingConfiguration;
                }
            }
        }

        return null;
    }

    @Override
    public int compareTo(Object o) {
        return PREFERED;
    }
}

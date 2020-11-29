package idea.plugin.jmeter.run;

import com.intellij.execution.Location;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.execution.junit.JavaRunConfigurationProducerBase;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import idea.plugin.jmeter.JmeterFileType;
import org.jetbrains.annotations.NotNull;

public class JmeterRunConfigurationProducer extends JavaRunConfigurationProducerBase<JmeterRunConfiguration> {

    @Override
    protected boolean setupConfigurationFromContext(@NotNull JmeterRunConfiguration runConfiguration, ConfigurationContext context, @NotNull Ref<PsiElement> psiElementRef) {
        Location location = context.getLocation();
        if (location == null) return false;

        VirtualFile testFile = location.getVirtualFile();

        if (testFile == null || !JmeterFileType.INSTANCE.equals(testFile.getFileType())) {
            return false;
        }

        runConfiguration.setTestFile(testFile.getPath());
        runConfiguration.setName(testFile.getName());

        VirtualFile propertyFile = testFile.getParent().findChild("jmeter.properties");
        if (propertyFile != null) {
            runConfiguration.setPropertyFile(propertyFile.getPath());
        }

        String customParameters = "";

        VirtualFile systemPropertyFile = testFile.getParent().findChild("system.properties");
        if (systemPropertyFile != null) {
            customParameters += "--systemPropertyFile " + systemPropertyFile.getPath() + " ";
        }

        VirtualFile userPropertyFile = testFile.getParent().findChild("user.properties");
        if (userPropertyFile != null) {
            customParameters += "--addprop " + userPropertyFile.getPath() + " ";
        }

        VirtualFile testPropertyFile = testFile.getParent().findChild(testFile.getNameWithoutExtension() + ".properties");
        if (testPropertyFile != null) {
            customParameters += "--addprop " + testPropertyFile.getPath() + " ";
        }

        runConfiguration.setCustomParameters(customParameters.trim());
        return true;
    }

    @Override
    public boolean isConfigurationFromContext(@NotNull JmeterRunConfiguration runConfiguration, ConfigurationContext context) {
        Location location = context.getLocation();
        if (location == null) return false;

        VirtualFile file = location.getVirtualFile();
        if (file == null) return false;

        return FileUtil.toSystemIndependentName(file.getPath()).equals(FileUtil.toSystemIndependentName(runConfiguration.getTestFile()));
    }

}

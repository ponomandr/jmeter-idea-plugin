package idea.plugin.jmeter.run;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.SimpleConfigurationType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NotNullLazyValue;
import org.jetbrains.annotations.NotNull;

import static idea.plugin.jmeter.JmeterFileType.FILE_ICON;

public class JmeterConfigurationType extends SimpleConfigurationType {
    public static final String TYPE_ID = "idea.plugin.jmeter.run.JmeterConfigurationType";

    public JmeterConfigurationType() {
        super(TYPE_ID, "JMeter", "Configuration to run a JMeter test", NotNullLazyValue.createValue(() -> FILE_ICON));
    }

    @NotNull
    @Override
    public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
        JmeterRunConfiguration configuration = new JmeterRunConfiguration(project, this);
        return configuration;
    }

    public static JmeterConfigurationType getInstance() {
        return ConfigurationTypeUtil.findConfigurationType(JmeterConfigurationType.class);
    }

}

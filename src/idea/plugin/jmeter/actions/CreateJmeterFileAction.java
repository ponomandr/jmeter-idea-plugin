package idea.plugin.jmeter.actions;

import com.intellij.ide.actions.CreateElementActionBase;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import idea.plugin.jmeter.JmeterFileType;
import org.jetbrains.annotations.NotNull;

public class CreateJmeterFileAction extends CreateElementActionBase {

    public CreateJmeterFileAction() {
        super("Create JMeter File", JmeterFileType.INSTANCE.getDescription(), JmeterFileType.FILE_ICON);
    }

    @NotNull
    @Override
    protected PsiElement[] invokeDialog(Project project, PsiDirectory directory) {
        CreateElementActionBase.MyInputValidator validator = new CreateElementActionBase.MyInputValidator(project, directory);
        Messages.showInputDialog(project, "Enter a new file name:", "New JMeter File", Messages.getQuestionIcon(), "", validator);
        return validator.getCreatedElements();
    }

    @NotNull
    @Override
    protected PsiElement[] create(String newName, PsiDirectory directory) {
        String ext = '.' + JmeterFileType.INSTANCE.getDefaultExtension();
        String fileName = newName.endsWith(ext) ? newName : newName + ext;
        String testPlanName = newName.endsWith(ext) ? newName.substring(0, newName.length() - ext.length()) : newName;
        PsiFile file = PsiFileFactory.getInstance(directory.getProject()).createFileFromText(fileName, JmeterFileType.INSTANCE,
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<jmeterTestPlan version=\"1.2\" properties=\"2.6\">\n" +
                        "  <hashTree>\n" +
                        "    <TestPlan guiclass=\"TestPlanGui\" testclass=\"TestPlan\" testname=\"" + testPlanName + "\" enabled=\"true\">\n" +
                        "      <stringProp name=\"TestPlan.comments\"></stringProp>\n" +
                        "      <boolProp name=\"TestPlan.functional_mode\">false</boolProp>\n" +
                        "      <boolProp name=\"TestPlan.serialize_threadgroups\">false</boolProp>\n" +
                        "      <elementProp name=\"TestPlan.user_defined_variables\" elementType=\"Arguments\" guiclass=\"ArgumentsPanel\" testclass=\"Arguments\" testname=\"User Defined Variables\" enabled=\"true\">\n" +
                        "        <collectionProp name=\"Arguments.arguments\"/>\n" +
                        "      </elementProp>\n" +
                        "      <stringProp name=\"TestPlan.user_define_classpath\"></stringProp>\n" +
                        "    </TestPlan>\n" +
                        "    <hashTree/>\n" +
                        "  </hashTree>\n" +
                        "</jmeterTestPlan>\n");
        return new PsiElement[]{directory.add(file)};
    }

    @Override
    protected String getErrorTitle() {
        return "Cannot create JMeter File";
    }

    @Override
    protected String getCommandName() {
        return "Create JMeter File";
    }

    @Override
    protected String getActionName(PsiDirectory psiDirectory, String s) {
        return "JMeter File";
    }
}

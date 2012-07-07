package idea.plugin.jmeter.run;

import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.HyperlinkInfo;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.psi.search.GlobalSearchScope;
import idea.plugin.jmeter.run.tailer.Tailer;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.io.File;
import java.util.List;

class JmeterConsoleView extends JSplitPane implements ConsoleView, DataProvider {
    private final ConsoleViewImpl console;
    private final File logFile;
    private final JTree testTree;
    private final DefaultMutableTreeNode root;
    private final DefaultTreeModel treeModel;

    public JmeterConsoleView(Project project, File logFile, JmeterRunConfiguration runConfiguration) {
        super(JSplitPane.HORIZONTAL_SPLIT);
        console = new ConsoleViewImpl(project, GlobalSearchScope.projectScope(project), false, null);
        this.logFile = logFile;
        console.addCustomConsoleAction(new RunJmeterGuiAction(runConfiguration));
        root = new DefaultMutableTreeNode("root");
        treeModel = new DefaultTreeModel(root);
        testTree = new JTree(treeModel);
        testTree.expandPath(new TreePath(root));
        testTree.setRootVisible(false);
        testTree.setCellRenderer(new CustomIconRenderer());
        add(new JScrollPane(testTree));
        add(console);
        setDividerLocation(400);
    }

    @Override
    public void print(String s, ConsoleViewContentType contentType) {
        console.print(s, contentType);
    }

    @Override
    public void clear() {
        console.clear();
        root.removeAllChildren();
        treeModel.nodeStructureChanged(root);
    }

    @Override
    public void scrollTo(int offset) {
        console.scrollTo(offset);
    }

    @Override
    public void attachToProcess(ProcessHandler processHandler) {
        final Tailer tailer = Tailer.create(logFile, new LogfileTailerListener(this), 500);

        processHandler.addProcessListener(new ProcessAdapter() {
            @Override
            public void processWillTerminate(ProcessEvent event, boolean willBeDestroyed) {
                // Give tailer a chance to finish its job
                sleep(tailer.getDelay() * 3 / 2);
                tailer.stop();
            }
        });

        console.attachToProcess(processHandler);
    }

    @Override
    public void setOutputPaused(boolean value) {
    }

    @Override
    public boolean isOutputPaused() {
        return false;
    }

    @Override
    public boolean hasDeferredOutput() {
        return false;
    }

    @Override
    public void performWhenNoDeferredOutput(Runnable runnable) {
        console.performWhenNoDeferredOutput(runnable);
    }

    @Override
    public void setHelpId(String helpId) {
        console.setHelpId(helpId);
    }

    @Override
    public void addMessageFilter(Filter filter) {
        console.addMessageFilter(filter);
    }

    @Override
    public void printHyperlink(String hyperlinkText, HyperlinkInfo info) {
        console.printHyperlink(hyperlinkText, info);
    }

    @Override
    public int getContentSize() {
        return console.getContentSize();
    }

    @Override
    public boolean canPause() {
        return false;
    }

    @NotNull
    @Override
    public AnAction[] createConsoleActions() {
        return console.createConsoleActions();
    }

    @Override
    public void allowHeavyFilters() {
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public JComponent getPreferredFocusableComponent() {
        return console.getPreferredFocusableComponent();
    }

    @Override
    public void dispose() {
        console.dispose();
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
        }
    }

    @Override
    public Object getData(@NonNls String dataId) {
        if (LangDataKeys.CONSOLE_VIEW.is(dataId)) {
            return this;
        }
        return null;
    }

    public void addTestOk(final String sampleName) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
//                PoolOfTestIcons.PASSED_ICON
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(new TestResult(sampleName, TestState.ok));
                treeModel.insertNodeInto(node, root, root.getChildCount());
                treeModel.nodeStructureChanged(root);
            }
        });
    }

    public void addTestFailed(final String sampleName, final List<Assertion> failedAssertions) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                DefaultMutableTreeNode testNode = new DefaultMutableTreeNode(new TestResult(sampleName, TestState.failed));
                treeModel.insertNodeInto(testNode, root, root.getChildCount());
                for (Assertion assertion : failedAssertions) {
                    TestState type = assertion.isError() ? TestState.error : TestState.failed;
                    DefaultMutableTreeNode assertionNode = new DefaultMutableTreeNode(new TestResult(assertion.getName(), type));
                    treeModel.insertNodeInto(assertionNode, testNode, testNode.getChildCount());
                }
                treeModel.nodeStructureChanged(root);
            }
        });
    }

    static class CustomIconRenderer extends DefaultTreeCellRenderer {
        private static final Icon success = IconLoader.getIcon("/icons/icon_success_sml.gif");
        private static final Icon warning = IconLoader.getIcon("/icons/icon_warning_sml.gif");
        private static final Icon error = IconLoader.getIcon("/icons/icon_error_sml.gif");

        public Component getTreeCellRendererComponent(JTree tree,
                                                      Object value, boolean sel, boolean expanded, boolean leaf,
                                                      int row, boolean hasFocus) {

            super.getTreeCellRendererComponent(tree, value, sel,
                    expanded, leaf, row, hasFocus);

            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            Object userObject = node.getUserObject();
            if (userObject instanceof TestResult) {
                TestResult testResult = (TestResult) userObject;
                setText(testResult.getSampleName());
                switch (testResult.getTestState()) {
                    case ok:
                        setIcon(success);
                        break;
                    case failed:
                        setIcon(warning);
                        break;
                    case error:
                        setIcon(error);
                        break;
                }
            }
            return this;
        }
    }

    public enum TestState {
        ok, failed, error
    }

    public static class TestResult {
        private final String sampleName;
        private final TestState testState;

        public TestResult(String sampleName, TestState testState) {
            this.sampleName = sampleName;
            this.testState = testState;
        }

        public String getSampleName() {
            return sampleName;
        }

        public TestState getTestState() {
            return testState;
        }
    }
}
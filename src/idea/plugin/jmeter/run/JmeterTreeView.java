package idea.plugin.jmeter.run;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class JmeterTreeView extends JPanel {

    private final DefaultMutableTreeNode root;
    private final DefaultTreeModel treeModel;

    public JmeterTreeView() {
        super(new BorderLayout());
        root = new DefaultMutableTreeNode();
        treeModel = new DefaultTreeModel(root);
        JTree testTree = new JTree(treeModel);
        testTree.expandPath(new TreePath(root));
        testTree.setRootVisible(false);
        testTree.setCellRenderer(new CustomIconRenderer());
        testTree.setShowsRootHandles(true);
        add(testTree, BorderLayout.CENTER);
    }

    public void clear() {
        root.removeAllChildren();
        treeModel.nodeStructureChanged(root);
    }

    public void addTestOk(final String sampleName) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(new TestResult(sampleName, TestResult.State.success));
                treeModel.insertNodeInto(node, root, root.getChildCount());
                treeModel.nodeStructureChanged(root);
            }
        });
    }

    public void addTestFailed(final String sampleName, final java.util.List<Assertion> failedAssertions) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                DefaultMutableTreeNode testNode = new DefaultMutableTreeNode(new TestResult(sampleName, TestResult.State.failed));
                treeModel.insertNodeInto(testNode, root, root.getChildCount());
                for (Assertion assertion : failedAssertions) {
                    TestResult.State type = assertion.isError() ? TestResult.State.error : TestResult.State.failed;
                    DefaultMutableTreeNode assertionNode = new DefaultMutableTreeNode(new TestResult(assertion.getName(), type));
                    treeModel.insertNodeInto(assertionNode, testNode, testNode.getChildCount());
                }
                treeModel.nodeStructureChanged(root);
            }
        });
    }


    private static class CustomIconRenderer extends DefaultTreeCellRenderer {
        private static final Map<TestResult.State, Icon> icons = new HashMap<TestResult.State, Icon>() {{
            put(TestResult.State.success, IconLoader.getIcon("/icons/icon_success_sml.gif"));
            put(TestResult.State.failed, IconLoader.getIcon("/icons/icon_warning_sml.gif"));
            put(TestResult.State.error, IconLoader.getIcon("/icons/icon_error_sml.gif"));
        }};

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            Object userObject = node.getUserObject();
            if (userObject instanceof TestResult) {
                TestResult testResult = (TestResult) userObject;
                setText(testResult.simpleName());
                setIcon(icons.get(testResult.state()));
            }
            return this;
        }
    }

}

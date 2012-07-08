package idea.plugin.jmeter.run;

import com.intellij.openapi.util.IconLoader;
import idea.plugin.jmeter.domain.Assertion;
import idea.plugin.jmeter.domain.SampleResult;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
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

    public JmeterTreeView(final JmeterConsoleView jmeterConsoleView) {
        super(new BorderLayout());
        root = new DefaultMutableTreeNode();
        treeModel = new DefaultTreeModel(root);
        JTree testTree = new JTree(treeModel);
        testTree.expandPath(new TreePath(root));
        testTree.setRootVisible(false);
        testTree.setCellRenderer(new CustomIconRenderer());
        testTree.setShowsRootHandles(true);
        testTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                Object pathComponent = e.getPath().getLastPathComponent();
                if (pathComponent instanceof DefaultMutableTreeNode) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) pathComponent;
                    Object userObject = node.getUserObject();
                    if (userObject instanceof Assertion) {
                        jmeterConsoleView.onAssertionSelected((Assertion) userObject);
                    }
                    if (userObject instanceof SampleResult) {
                        jmeterConsoleView.onSampleResultSelected((SampleResult) userObject);
                    }
                }
            }
        });
        add(testTree, BorderLayout.CENTER);
    }

    public void clear() {
        root.removeAllChildren();
        treeModel.nodeStructureChanged(root);
    }

    public void addTestFailed(final SampleResult sampleResult) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                DefaultMutableTreeNode testNode = new DefaultMutableTreeNode(sampleResult);
                treeModel.insertNodeInto(testNode, root, root.getChildCount());
                for (Assertion assertion : sampleResult.getAssertions()) {
                    DefaultMutableTreeNode assertionNode = new DefaultMutableTreeNode(assertion);
                    treeModel.insertNodeInto(assertionNode, testNode, testNode.getChildCount());
                }
                treeModel.nodeStructureChanged(root);
            }
        });
    }


    private static class CustomIconRenderer extends DefaultTreeCellRenderer {
        private static final Map<SampleResult.State, Icon> icons = new HashMap<SampleResult.State, Icon>() {{
            put(SampleResult.State.success, IconLoader.getIcon("/icons/icon_success_sml.gif"));
            put(SampleResult.State.failed, IconLoader.getIcon("/icons/icon_warning_sml.gif"));
            put(SampleResult.State.error, IconLoader.getIcon("/icons/icon_error_sml.gif"));
        }};

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            Object userObject = node.getUserObject();
            if (userObject instanceof SampleResult) {
                SampleResult testResult = (SampleResult) userObject;
                setText(testResult.getName());
                setIcon(icons.get(testResult.getState()));
            }
            if (userObject instanceof Assertion) {
                Assertion assertion = (Assertion) userObject;
                setText(assertion.getName());
                setIcon(icons.get(assertion.getState()));
            }
            return this;
        }
    }

}

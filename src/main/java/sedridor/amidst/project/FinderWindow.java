package sedridor.amidst.project;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import sedridor.amidst.Amidst;
import sedridor.amidst.gui.AmidstMenu;

public class FinderWindow extends JFrame {

    public static FinderWindow instance;

    private Container pane;

    public Project curProject;

    public static boolean dataCollect;

    public final AmidstMenu menuBar;

    public FinderWindow() {
        super("ForgeAMIDST " + Amidst.version());
        setSize(1000, 800);
        this.pane = getContentPane();
        this.pane.setLayout(new BorderLayout());
        setJMenuBar(this.menuBar = new AmidstMenu(this));
        setVisible(true);
        setIconImage(Amidst.icon);
        instance = this;
        addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                FinderWindow.this.dispose();
            }
        });
    }

    public void clearProject() {
        if (this.curProject != null) {
            removeKeyListener(this.curProject.getKeyListener());
            this.curProject.dispose();
            this.pane.remove(this.curProject);
            System.gc();
        }
    }

    public void setProject(Project ep) {
        this.menuBar.mapMenu.setEnabled(true);
        this.menuBar.reloadMap.setEnabled(true);
        this.curProject = ep;
        addKeyListener(ep.getKeyListener());
        this.pane.add(this.curProject, "Center");
        validate();
    }
}

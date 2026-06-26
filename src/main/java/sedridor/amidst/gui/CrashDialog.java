package sedridor.amidst.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

import sedridor.amidst.logging.LogRecorder;

public class CrashDialog extends JFrame {

    public CrashDialog(String message) {
        super("AMIDST encountered an unexpected error.");
        Container contentPane = new JPanel();
        contentPane.setLayout(null);
        setContentPane(contentPane);
        add(new JLabel("AMIDST has crashed with the following message:", 2)).setBounds(5, 5, 235, 14);
        add(new JLabel(message, 2)).setBounds(5, 23, 474, 14);
        JTextArea logText = new JTextArea(LogRecorder.getContents());
        logText.setFont(new Font("arial", 0, 10));
        logText.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logText);
        scrollPane.setHorizontalScrollBarPolicy(30);
        scrollPane.setBounds(5, 41, 474, 315);
        scrollPane.setBorder(new LineBorder(Color.darkGray, 1));
        add(scrollPane);
        setSize(500, 400);
        setVisible(true);
        addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                CrashDialog.this.dispose();
            }
        });
    }
}

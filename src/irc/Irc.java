/***
 * Irc class : simple implementation of a chat using JAVANAISE
 * Contact: 
 *
 * Authors: 
 */

package irc;

import jvn.JvnObject;
import jvn.exception.JvnException;
import jvn.impl.JvnServerImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;


public class Irc {
    public JTextArea text;
    public JTextField data;
    public JTextField salle;

    JFrame frame;
    JPanel buttonPanel;
    JPanel sallePanel;
    Button connectSalle;
    Button disconnectSalle;

    JvnObject sentence;

    JvnServerImpl js;


    /**
     * main method
     * create a JVN object nammed IRC for representing the Chat application
     **/
    public static void main(String argv[]) {
        try {

            // initialize JVN
//            JvnServerImpl js = JvnServerImpl.jvnGetServer();

            // create the graphical part of the Chat application
            new Irc();

        } catch (Exception e) {
            System.out.println("IRC problem : " + e.getMessage());
        }
    }

    /**
     * IRC Constructor
     **/
    public Irc() {
        //sentence = jo;

        try {

            // initialize JVN
            js = JvnServerImpl.jvnGetServer();
        } catch (Exception e) {

        }
        frame = new JFrame();
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sallePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        frame.setSize(new Dimension(400, 300));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout(0, 0));

        salle = new JTextField("IRC");
        text = new JTextArea();
        data = new JTextField();


        text.setEditable(false);
        text.setForeground(Color.red);
        text.setBackground(Color.black);

        text.setPreferredSize(new Dimension(100, 100));
        data.setPreferredSize(new Dimension(100, 100));
        salle.setPreferredSize(new Dimension(180, 20));

        frame.add(data, BorderLayout.CENTER);
        frame.add(text, BorderLayout.WEST);

        ButtonAction buttonAction = new ButtonAction(this);

        connectSalle = new Button("Connect to..");
        connectSalle.setPreferredSize(new Dimension(100, 20));
        connectSalle.setActionCommand("enterSalle");
        connectSalle.addActionListener(buttonAction);

        disconnectSalle = new Button("Disconnect..");
        disconnectSalle.setPreferredSize(new Dimension(100, 20));
        disconnectSalle.setActionCommand("exitSalle");
        disconnectSalle.addActionListener(buttonAction);

        sallePanel.add(salle);
        sallePanel.add(connectSalle);
        sallePanel.add(disconnectSalle);


        Button read_button = new Button("read");
        read_button.setActionCommand("readListener");
        read_button.addActionListener(buttonAction);
        buttonPanel.add(read_button);

        Button write_button = new Button("write");
        write_button.setActionCommand("writeListener");
        write_button.addActionListener(buttonAction);
        buttonPanel.add(write_button);

        //frame.setSize(545,201);
        frame.add(sallePanel, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.setVisible(true);


    }
}

class ButtonAction implements ActionListener {
    Irc irc;

    public ButtonAction(Irc i) {
        irc = i;
    }

    /**
     * Management of user events
     **/
    public void actionPerformed(ActionEvent e) {
        try {
            switch (e.getActionCommand()) {
                case "writeListener":

                    // lock the object in write mode
                    irc.sentence.jvnLockWrite();

                    // invoke the method
                    ((Sentence) (irc.sentence.jvnGetObjectState())).write(irc.data.getText());

                    // unlock the object
                    irc.sentence.jvnUnLock();

                    break;
                case "readListener":
                    // lock the object in read mode
                    irc.sentence.jvnLockRead();

                    // invoke the method
                    String s = ((Sentence) (irc.sentence.jvnGetObjectState())).read();

                    // unlock the object
                    irc.sentence.jvnUnLock();

                    // display the read value
                    irc.data.setText(s);
                    irc.text.append(s + "\n");

                    break;
                case "enterSalle":
                    // look up the IRC object in the JVN server
                    // if not found, create it, and register it in the JVN server
                    irc.sentence = irc.js.jvnLookupObject(irc.salle.getText());

                    if (irc.sentence == null) {
                        irc.sentence = irc.js.jvnCreateObject((Serializable) new Sentence());
                        // after creation, I have a write lock on the object
                        irc.sentence.jvnUnLock();
                        irc.js.jvnRegisterObject("IRC", irc.sentence);
                        irc.salle.setEditable(false);
                        irc.connectSalle.setLabel("Terminate...");

                    }

                    break;
                case "exitSalle":
                    irc.sentence=null;
                    irc.text.setText("");
                    irc.data.setText("");
                    break;
            }
        } catch (JvnException je) {
            System.out.println("IRC problem  : " + je.getMessage());
        }


    }

    class JvnMouseClick extends MouseAdapter {
        Irc irc;

        public JvnMouseClick(Irc irc) {
            this.irc = irc;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            if (e.getClickCount() == 2) {
                irc.salle.setEditable(true);
                irc.connectSalle.setLabel("Connect to..");
                irc.connectSalle.setEnabled(true);
            }
        }
    }
}




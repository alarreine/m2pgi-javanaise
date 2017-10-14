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
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
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
    Button write_button;
    Button read_button;
    Button unlock_button;

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
            Irc irc = new Irc();

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
            System.out.println("IRC problem : " + e.getMessage());
        }
        frame = new JFrame();
        frame.addWindowListener(new JvnWindowsListener(this));
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
        disconnectSalle.setEnabled(false);

        sallePanel.add(salle);
        sallePanel.add(connectSalle);
        sallePanel.add(disconnectSalle);

        read_button = new Button("read");
        read_button.setActionCommand("readListener");
        read_button.addActionListener(buttonAction);
        read_button.setEnabled(false);
        buttonPanel.add(read_button);

        write_button = new Button("write");
        write_button.setActionCommand("writeListener");
        write_button.addActionListener(buttonAction);
        write_button.setEnabled(false);
        buttonPanel.add(write_button);

        unlock_button = new Button("unlock");
        unlock_button.setActionCommand("unlockListener");
        unlock_button.addActionListener(buttonAction);
        unlock_button.setEnabled(false);
        buttonPanel.add(unlock_button);

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


                    break;
                case "readListener":
                    // lock the object in read mode
                    irc.sentence.jvnLockRead();

                    // invoke the method
                    String s = ((Sentence) (irc.sentence.jvnGetObjectState())).read();

                    // display the read value
                    irc.data.setText(s);
                    irc.text.append(s + "\n");

                    break;
                case "enterSalle":
                    // look up the IRC object in the JVN server
                    // if not found, create it, and register it in the JVN server
                    JvnObject jo = irc.js.jvnLookupObject(irc.salle.getText());

                    if (jo == null) {
                        jo = irc.js.jvnCreateObject((Serializable) new Sentence());
                        // after creation, I have a write lock on the object
                        jo.jvnUnLock();
                        irc.js.jvnRegisterObject(irc.salle.getText(), jo);
                    }
                    irc.sentence=jo;
                    irc.salle.setEnabled(false);
                    irc.connectSalle.setEnabled(false);
                    irc.disconnectSalle.setEnabled(true);

                    irc.read_button.setEnabled(true);
                    irc.write_button.setEnabled(true);
                    irc.unlock_button.setEnabled(true);

                    break;
                case "exitSalle":
                    irc.sentence=null;
                    irc.text.setText("");
                    irc.data.setText("");

                    irc.salle.setEnabled(true);
                    irc.connectSalle.setEnabled(true);
                    irc.disconnectSalle.setEnabled(false);
                    irc.read_button.setEnabled(false);
                    irc.write_button.setEnabled(false);
                    irc.unlock_button.setEnabled(false);
                    break;
                case "unlockListener":
                    irc.sentence.jvnUnLock();
                    break;
            }

        } catch (JvnException je) {
            System.out.println("IRC problem  : " + je.getMessage());
        }


    }
}

class JvnWindowsListener implements WindowListener {
    Irc irc;
    public JvnWindowsListener(Irc irc) {
        this.irc=irc;
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        try {
            irc.js.jvnTerminate();
        } catch (Exception ex) {

        }

    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
//
//        @Override
//        public void windowClosing(WindowEvent e) {
//            System.out.println("HOLA");
//            super.windowClosing(e);
//            System.out.println("ASDASD");
//            try{
//                irc.js.jvnTerminate();
//            }catch (Exception ex){
//
//            }
//
//        }
}




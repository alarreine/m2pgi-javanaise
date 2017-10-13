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
import java.io.Serializable;


public class Irc {
    public JTextArea text;
    public JTextField data;
    public JTextField salle;

    JFrame frame;
    JPanel buttonPanel;
    JPanel sallePanel;

    JvnObject sentence;


    /**
     * main method
     * create a JVN object nammed IRC for representing the Chat application
     **/
    public static void main(String argv[]) {
        try {

            // initialize JVN
            JvnServerImpl js = JvnServerImpl.jvnGetServer();

            // look up the IRC object in the JVN server
            // if not found, create it, and register it in the JVN server
            JvnObject jo = js.jvnLookupObject("IRC");

            if (jo == null) {
                jo = js.jvnCreateObject((Serializable) new Sentence());
                // after creation, I have a write lock on the object
                jo.jvnUnLock();
                js.jvnRegisterObject("IRC", jo);
            }
            // create the graphical part of the Chat application
            new Irc(jo);

        } catch (Exception e) {
            System.out.println("IRC problem : " + e.getMessage());
        }
    }

    /**
     * IRC Constructor
     *
     * @param jo the JVN object representing the Chat
     **/
    public Irc(JvnObject jo) {
        sentence = jo;
        frame = new JFrame();
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sallePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        //frame.setLayout(new GridLayout(1,1));
        //frame.setBounds(100, 100, 200, 300);
        frame.setSize(new Dimension(400,300));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout(0, 0));

        salle= new JTextField("IRC");
        text = new JTextArea();
        data = new JTextField();

        text.setEditable(false);
        text.setForeground(Color.red);
        text.setBackground(Color.black);

        text.setPreferredSize(new Dimension(100, 100));
        data.setPreferredSize(new Dimension(100, 100));
        salle.setPreferredSize(new Dimension(250,20));

        frame.add(data, BorderLayout.CENTER);
        frame.add(text, BorderLayout.WEST);

        Button changeSalle = new Button("Change Chat");
        changeSalle.setPreferredSize(new Dimension(100,20));

        Button read_button = new Button("read");
        read_button.addActionListener(new readListener(this));
        buttonPanel.add(read_button);

        Button write_button = new Button("write");
        write_button.addActionListener(new writeListener(this));
        buttonPanel.add(write_button);
        sallePanel.add(salle);
        sallePanel.add(changeSalle);
        //frame.setSize(545,201);
        frame.add(sallePanel, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }
}


/**
 * Internal class to manage user events (read) on the CHAT application
 **/
class readListener implements ActionListener {
    Irc irc;

    public readListener(Irc i) {
        irc = i;
    }

    /**
     * Management of user events
     **/
    public void actionPerformed(ActionEvent e) {
        try {
            // lock the object in read mode
            irc.sentence.jvnLockRead();

            // invoke the method
            String s = ((Sentence) (irc.sentence.jvnGetObjectState())).read();

            // unlock the object
            irc.sentence.jvnUnLock();

            // display the read value
            irc.data.setText(s);
            irc.text.append(s + "\n");
        } catch (JvnException je) {
            System.out.println("IRC problem : " + je.getMessage());
        }
    }
}

/**
 * Internal class to manage user events (write) on the CHAT application
 **/
class writeListener implements ActionListener {
    Irc irc;

    public writeListener(Irc i) {
        irc = i;
    }

    /**
     * Management of user events
     **/
    public void actionPerformed(ActionEvent e) {
        try {
            // get the value to be written from the buffer
            String s = irc.data.getText();

            // lock the object in write mode
            irc.sentence.jvnLockWrite();

            // invoke the method
            ((Sentence) (irc.sentence.jvnGetObjectState())).write(s);

            // unlock the object
            irc.sentence.jvnUnLock();
        } catch (JvnException je) {
            System.out.println("IRC problem  : " + je.getMessage());
        }
    }
}




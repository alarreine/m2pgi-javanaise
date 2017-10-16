/***
 * JAVANAISE Implementation
 * JvnServerImpl class
 * Contact: 
 *
 * Authors: 
 */

package jvn.impl;

import jvn.*;
import jvn.exception.JvnException;

import java.io.Serializable;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.logging.Logger;

public class JvnServerImpl extends UnicastRemoteObject implements JvnLocalServer, JvnRemoteServer {

    // A JVN server is managed as a singleton
    private static JvnServerImpl js = null;
    private HashMap<Integer, JvnObject> jvnObjects = null;

    private JvnRemoteCoord jvnCoordinator = null;

    private final static Logger loggerJvnServer = Logger.getLogger("jvn.impl.JvnServerImpl");

    /**
     * Default constructor
     *
     * @throws JvnException
     **/
    private JvnServerImpl() throws Exception {
        super();
        jvnObjects = new HashMap<Integer, JvnObject>();
        try {
            // call the coordinator
            jvnCoordinator = (JvnRemoteCoord) Naming.lookup("Coordinator");
            loggerJvnServer.info("Connected to coordinator. Ready....");
        } catch (NotBoundException e) {
            loggerJvnServer.severe("Coordinator not found");
        }
    }

    /**
     * Static method allowing an application to get a reference to a JVN server
     * instance
     *
     * @throws JvnException
     **/
    public static JvnServerImpl jvnGetServer() {
        if (js == null) {
            try {
                js = new JvnServerImpl();
            } catch (Exception e) {
                loggerJvnServer.severe("We can't initialize the JvnServerImpl. Msg:" + e.getMessage());
                return null;
            }
        }
        return js;
    }

    /**
     * The JVN service is not used anymore
     *
     * @throws JvnException
     **/
    public void jvnTerminate() throws JvnException {
        try {
            jvnCoordinator.jvnTerminate(this);
        } catch (Exception e) {
            loggerJvnServer.severe("We can't terminate. Error: " + e.getMessage());
        }

    }

    /**
     * creation of a JVN object
     *
     * @param o : the JVN object state
     * @throws JvnException
     **/
    public JvnObject jvnCreateObject(Serializable o) throws JvnException {
        int id;
        try {
            id = jvnCoordinator.jvnGetObjectId();
            return new JvnInterceptorImpl(id, o);
        } catch (RemoteException e) {
            loggerJvnServer.severe("We can't get a new ObjectID. Msg:" + e.getMessage());
            return null;
        }
    }

    /**
     * Associate a symbolic name with a JVN object
     *
     * @param jon : the JVN object name
     * @param jo  : the JVN object
     * @throws JvnException
     **/
    public void jvnRegisterObject(String jon, JvnObject jo) throws JvnException {
        try {

            jvnCoordinator.jvnRegisterObject(jon, jo, this);

            // Only put in cache after successful registration
            jvnObjects.put(jo.jvnGetObjectId(), jo);
        } catch (RemoteException e) {
            loggerJvnServer.severe("Network error while registering object. Msg:" + e.getMessage());
            throw new JvnException("Network error while registering object!\n" + e);
        }
    }

    /**
     * Provide the reference of a JVN object beeing given its symbolic name
     *
     * @param jon : the JVN object name
     * @return the JVN object
     * @throws JvnException
     **/
    public JvnObject jvnLookupObject(String jon) throws JvnException {

        JvnObject o = null;
        try {
            o = jvnCoordinator.jvnLookupObject(jon, this);
            if (o != null) {

                jvnObjects.put(o.jvnGetObjectId(), o);
                ((JvnInterceptorImpl) o).state = JvnState.NL;
            }
        } catch (RemoteException e) {
            loggerJvnServer.severe("We can't find the object " + jon + " . Msg:" + e.getMessage());
        }

        return o;
    }

    /**
     * Get a Read lock on a JVN object
     *
     * @param joi : the JVN object identification
     * @return the current JVN object state
     * @throws JvnException
     **/
    public Serializable jvnLockRead(int joi) throws JvnException {
        if (jvnObjects.containsKey(joi)) {
            try {
                return jvnCoordinator.jvnLockRead(joi, this);
            } catch (RemoteException e) {
                loggerJvnServer.severe("We can't lock Read the objectID:" + joi + ". Msg:" + e.getMessage());
                throw new JvnException("RMI Exception!\n" + e);
            }
        } else {
            loggerJvnServer.info("Unknown object ID:" + joi);
            throw new JvnException("Unknown object ID!");
        }

    }

    /**
     * Get a Write lock on a JVN object
     *
     * @param joi : the JVN object identification
     * @return the current JVN object state
     * @throws JvnException
     **/
    public Serializable jvnLockWrite(int joi) throws JvnException {
        if (jvnObjects.containsKey(joi)) {
            try {
                return jvnCoordinator.jvnLockWrite(joi, this);
            } catch (RemoteException e) {
                loggerJvnServer.severe("We can't lockWrite the objectID:" + joi + ". Msg:" + e.getMessage());
                throw new JvnException("RMI Exception!\n" + e);
            }
        } else {
            loggerJvnServer.info("Unknown object ID:" + joi);
            throw new JvnException("Unknown object ID!");
        }

    }

    /**
     * Invalidate the Read lock of the JVN object identified by id called by the
     * JvnCoord
     *
     * @param joi : the JVN object id
     * @return void
     * @throws java.rmi.RemoteException,JvnException
     **/
    public void jvnInvalidateReader(int joi) throws java.rmi.RemoteException, JvnException {
        jvnObjects.get(joi).jvnInvalidateReader();
    }

    ;

    /**
     * Invalidate the Write lock of the JVN object identified by id
     *
     * @param joi : the JVN object id
     * @return the current JVN object state
     * @throws java.rmi.RemoteException,JvnException
     **/
    public Serializable jvnInvalidateWriter(int joi) throws java.rmi.RemoteException, JvnException {
        return jvnObjects.get(joi).jvnInvalidateWriter();
    }

    ;

    /**
     * Reduce the Write lock of the JVN object identified by id
     *
     * @param joi : the JVN object id
     * @return the current JVN object state
     * @throws java.rmi.RemoteException,JvnException
     **/
    public Serializable jvnInvalidateWriterForReader(int joi) throws java.rmi.RemoteException, JvnException {

        return jvnObjects.get(joi).jvnInvalidateWriterForReader();
    }

    ;

}
/***
 * JAVANAISE Implementation
 * JvnServerImpl class
 * Contact: 
 *
 * Authors: 
 */

package jvn.impl;

import jvn.JvnCacheObject;
import jvn.JvnObject;
import jvn.JvnRemoteCoord;
import jvn.JvnRemoteServer;
import jvn.exception.JvnException;

import java.io.Serializable;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
import java.util.logging.Logger;


public class JvnCoordImpl
        extends UnicastRemoteObject
        implements JvnRemoteCoord {

    private static int nextObjectId;

    private static Registry registry;
    private static JvnCoordImpl coord;
    private static Hashtable<Integer, JvnCacheObject> mainCache;

    public static Logger logger = Logger.getLogger("jvn.impl.JvnCoordImpl");


    public static void main(String argv[]) throws Exception {
        try {
            if (System.getSecurityManager() == null) {
                System.setProperty("java.security.policy", "file:./java.policy");
                System.setSecurityManager(new SecurityManager());

            }
            //Initialization
            registry = LocateRegistry.createRegistry(1099);
            coord = new JvnCoordImpl();
            mainCache = new Hashtable<>();


            System.out.println("Coordinator ready");


        } catch (Exception e) {
            logger.severe("Error while initiating Coordinator. " + e.getMessage());

        }
    }

    /**
     * Default constructor
     *
     * @throws JvnException
     **/
    private JvnCoordImpl() throws Exception {
        try {
            nextObjectId = -1;
            registry.rebind("Coordinator", this);

        } catch (Exception e) {

        }
    }


    /**
     * Allocate a NEW JVN object id (usually allocated to a
     * newly created JVN object)
     *
     * @throws java.rmi.RemoteException,JvnException
     **/
    public int jvnGetObjectId()
            throws java.rmi.RemoteException, JvnException {
        // to be completed
        return 0;
    }

    /**
     * Associate a symbolic name with a JVN object
     *
     * @param jon : the JVN object name
     * @param jo  : the JVN object
     * @param joi : the JVN object identification
     * @param js  : the remote reference of the JVNServer
     * @throws java.rmi.RemoteException,JvnException
     **/
    public void jvnRegisterObject(String jon, JvnObject jo, JvnRemoteServer js)
            throws java.rmi.RemoteException, JvnException {
        // to be completed
    }

    /**
     * Get the reference of a JVN object managed by a given JVN server
     *
     * @param jon : the JVN object name
     * @param js  : the remote reference of the JVNServer
     * @throws java.rmi.RemoteException,JvnException
     **/
    public JvnObject jvnLookupObject(String jon, JvnRemoteServer js)
            throws java.rmi.RemoteException, JvnException {
        // to be completed
        return null;
    }

    /**
     * Get a Read lock on a JVN object managed by a given JVN server
     *
     * @param joi : the JVN object identification
     * @param js  : the remote reference of the server
     * @return the current JVN object state
     * @throws java.rmi.RemoteException, JvnException
     **/
    public Serializable jvnLockRead(int joi, JvnRemoteServer js)
            throws java.rmi.RemoteException, JvnException {
        // to be completed
        return null;
    }

    /**
     * Get a Write lock on a JVN object managed by a given JVN server
     *
     * @param joi : the JVN object identification
     * @param js  : the remote reference of the server
     * @return the current JVN object state
     * @throws java.rmi.RemoteException, JvnException
     **/
    public Serializable jvnLockWrite(int joi, JvnRemoteServer js)
            throws java.rmi.RemoteException, JvnException {
        // to be completed
        return null;
    }

    /**
     * A JVN server terminates
     *
     * @param js : the remote reference of the server
     * @throws java.rmi.RemoteException, JvnException
     **/
    public void jvnTerminate(JvnRemoteServer js)
            throws java.rmi.RemoteException, JvnException {
        // to be completed
    }
}

 

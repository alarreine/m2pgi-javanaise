/***
 * JAVANAISE Implementation
 * JvnServerImpl class
 * Contact: 
 *
 * Authors: 
 */

package jvn.impl;

import jvn.JvnCacheObject;
import jvn.exception.JvnException;
import jvn.JvnObject;
import jvn.JvnRemoteCoord;
import jvn.JvnRemoteServer;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.io.Serializable;


public class JvnCoordImpl
        extends UnicastRemoteObject
        implements JvnRemoteCoord {

    private static Registry registry;
    private static JvnCoordImpl coord;


    public static void main(String argv[]) throws Exception {
        try {
            if (System.getSecurityManager() == null) {
                System.setProperty("java.security.policy","file:./java.policy");
                System.setSecurityManager(new SecurityManager());

            }

            registry = LocateRegistry.createRegistry(1099);

            coord = new JvnCoordImpl();

            System.out.println("Coordinator ready");




        }catch (Exception e){

        }
    }
    /**
     * Default constructor
     * @throws JvnException
     **/
    private JvnCoordImpl() throws Exception {
        try {
            registry.bind("Coordinator",this);

        }catch (Exception e){

        }
    }



    /**
     *  Allocate a NEW JVN object id (usually allocated to a
     *  newly created JVN object)
     * @throws java.rmi.RemoteException,JvnException
     **/
    public int jvnGetObjectId()
            throws java.rmi.RemoteException,JvnException {
        // to be completed
        return 0;
    }

    /**
     * Associate a symbolic name with a JVN object
     * @param jon : the JVN object name
     * @param jo  : the JVN object
     * @param joi : the JVN object identification
     * @param js  : the remote reference of the JVNServer
     * @throws java.rmi.RemoteException,JvnException
     **/
    public void jvnRegisterObject(String jon, JvnObject jo, JvnRemoteServer js)
            throws java.rmi.RemoteException,JvnException {
        // to be completed
    }

    /**
     * Get the reference of a JVN object managed by a given JVN server
     * @param jon : the JVN object name
     * @param js : the remote reference of the JVNServer
     * @throws java.rmi.RemoteException,JvnException
     **/
    public JvnObject jvnLookupObject(String jon, JvnRemoteServer js)
            throws java.rmi.RemoteException,JvnException {
        // to be completed
        return null;
    }

    /**
     * Get a Read lock on a JVN object managed by a given JVN server
     * @param joi : the JVN object identification
     * @param js  : the remote reference of the server
     * @return the current JVN object state
     * @throws java.rmi.RemoteException, JvnException
     **/
    public Serializable jvnLockRead(int joi, JvnRemoteServer js)
            throws java.rmi.RemoteException, JvnException{
        // to be completed
        return null;
    }

    /**
     * Get a Write lock on a JVN object managed by a given JVN server
     * @param joi : the JVN object identification
     * @param js  : the remote reference of the server
     * @return the current JVN object state
     * @throws java.rmi.RemoteException, JvnException
     **/
    public Serializable jvnLockWrite(int joi, JvnRemoteServer js)
            throws java.rmi.RemoteException, JvnException{
        // to be completed
        return null;
    }

    /**
     * A JVN server terminates
     * @param js  : the remote reference of the server
     * @throws java.rmi.RemoteException, JvnException
     **/
    public void jvnTerminate(JvnRemoteServer js)
            throws java.rmi.RemoteException, JvnException {
        // to be completed
    }
}

 

/***
 * JAVANAISE Implementation
 * JvnServerImpl class
 * Contact: 
 *
 * Authors: 
 */

package jvn.impl;

import java.io.Serializable;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

import jvn.JvnLocalServer;
import jvn.JvnObject;
import jvn.JvnRemoteCoord;
import jvn.JvnRemoteServer;
import jvn.JvnState;
import jvn.exception.JvnException;

public class JvnServerImpl extends UnicastRemoteObject implements JvnLocalServer, JvnRemoteServer {

	// A JVN server is managed as a singleton
	private static JvnServerImpl js = null;
	private HashMap<Integer, JvnObject> jvnObjects = null;

	private JvnRemoteCoord jvnCoordinator = null;

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
		} catch (NotBoundException e) {
			System.out.println("Coordinator not found");
			jvnCoordinator = null;
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
		// to be completed
	}

	/**
	 * creation of a JVN object
	 * 
	 * @param o
	 *            : the JVN object state
	 * @throws JvnException
	 **/
	public JvnObject jvnCreateObject(Serializable o) throws JvnException {
		int id;
		try {
			id = jvnCoordinator.jvnGetObjectId();
			return new JvnInterceptorImpl(id, o);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Associate a symbolic name with a JVN object
	 * 
	 * @param jon
	 *            : the JVN object name
	 * @param jo
	 *            : the JVN object
	 * @throws JvnException
	 **/
	public void jvnRegisterObject(String jon, JvnObject jo) throws JvnException {
		try {

			jvnCoordinator.jvnRegisterObject(jon, jo, this);

			// Only put in cache after successful registration
			jvnObjects.put(jo.jvnGetObjectId(), jo);
		} catch (RemoteException e) {
			throw new JvnException("Network error while registering object!\n" + e);
		}
	}

	/**
	 * Provide the reference of a JVN object beeing given its symbolic name
	 * 
	 * @param jon
	 *            : the JVN object name
	 * @return the JVN object
	 * @throws JvnException
	 **/
	public JvnObject jvnLookupObject(String jon) throws JvnException {

		JvnObject temp = null;
		try {
			temp = jvnCoordinator.jvnLookupObject(jon, this);
			if (temp != null) {

				jvnObjects.put(temp.jvnGetObjectId(), temp);
				((JvnInterceptorImpl) temp).state = JvnState.NL;
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		return temp;
	}

	/**
	 * Get a Read lock on a JVN object
	 * 
	 * @param joi
	 *            : the JVN object identification
	 * @return the current JVN object state
	 * @throws JvnException
	 **/
	public Serializable jvnLockRead(int joi) throws JvnException {
		if (jvnCoordinator == null) {
			throw new JvnException("There is no Coordinator!");
		} else if (jvnObjects.containsKey(joi)) {
			try {
				return jvnCoordinator.jvnLockRead(joi, this);
			} catch (RemoteException e) {
				throw new JvnException("RMI Exception!\n" + e);
			}
		} else {
			throw new JvnException("Unknown object ID!");
		}

	}

	/**
	 * Get a Write lock on a JVN object
	 * 
	 * @param joi
	 *            : the JVN object identification
	 * @return the current JVN object state
	 * @throws JvnException
	 **/
	public Serializable jvnLockWrite(int joi) throws JvnException {
		if (jvnCoordinator == null) {
			throw new JvnException("There is no Coordinator!");
		} else if (jvnObjects.containsKey(joi)) {
			try {
				return jvnCoordinator.jvnLockWrite(joi, this);
			} catch (RemoteException e) {
				throw new JvnException("RMI Exception!\n" + e);
			}
		} else {
			throw new JvnException("Unknown object ID!");
		}

	}

	/**
	 * Invalidate the Read lock of the JVN object identified by id called by the
	 * JvnCoord
	 * 
	 * @param joi
	 *            : the JVN object id
	 * @return void
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public void jvnInvalidateReader(int joi) throws java.rmi.RemoteException, JvnException {
		// to be completed
	};

	/**
	 * Invalidate the Write lock of the JVN object identified by id
	 * 
	 * @param joi
	 *            : the JVN object id
	 * @return the current JVN object state
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public Serializable jvnInvalidateWriter(int joi) throws java.rmi.RemoteException, JvnException {
		// to be completed
		return null;
	};

	/**
	 * Reduce the Write lock of the JVN object identified by id
	 * 
	 * @param joi
	 *            : the JVN object id
	 * @return the current JVN object state
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public Serializable jvnInvalidateWriterForReader(int joi) throws java.rmi.RemoteException, JvnException {
		// to be completed
		return null;
	};

}

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
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;
import java.util.logging.Logger;


public class JvnCoordImpl
        extends UnicastRemoteObject
        implements JvnRemoteCoord {

    private static int nextId;
    private final Lock lockNextId = new ReentrantLock();

    private static Registry registry;
    private static JvnCoordImpl coord;

    private static Map<Integer, JvnCacheObject> cacheObject;
    private static Map<String, JvnCacheObject> nameCacheObject;

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


            logger.info("Coordinator ready...");


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
            nextId = -1;
            registry.rebind("Coordinator", this);

            cacheObject = new ConcurrentHashMap<Integer, JvnCacheObject>();
            nameCacheObject = new ConcurrentHashMap<String, JvnCacheObject>();

            logger.info("Chache ready...");

        } catch (Exception e) {
            logger.severe("Unable to initialise the chache");
        }
    }


    /**
     * Allocate a NEW JVN object id (usually allocated to a
     * newly created JVN object)
     *
     * @return -1 If any error
     * @throws java.rmi.RemoteException,JvnException
     **/
    public int jvnGetObjectId()
            throws java.rmi.RemoteException, JvnException {
        int idNew = -1;
        lockNextId.lock();
        try {
            nextId++;
            idNew = nextId;
        } catch (Exception e) {
            logger.severe("Imposible to get a new object ID. " + e.getMessage());
            throw new JvnException("Imposible to get a new object ID. ");
        } finally {
            lockNextId.unlock();
        }

        return idNew;
    }

    /**
     * Associate a symbolic name with a JVN object
     *
     * @param jon : the JVN object name
     * @param jo  : the JVN object
     * @param js  : the remote reference of the JVNServer
     * @throws java.rmi.RemoteException,JvnException
     **/
    public void jvnRegisterObject(String jon, JvnObject jo, JvnRemoteServer js)
            throws java.rmi.RemoteException, JvnException {

        if (jo == null) throw new NullPointerException("The JvnObject is required");
        if (cacheObject.containsKey(jo.jvnGetObjectId()))
            throw new JvnException("The objectId required is already used");
        if (nameCacheObject.containsKey(jon)) throw new JvnException("The name required is already used");

        JvnCacheObject newObject = new JvnCacheObject(jo, JvnState.W, js);

        cacheObject.put(jo.jvnGetObjectId(), newObject);
        nameCacheObject.put(jon, newObject);
        logger.info("Object name: " + jon + " id: " + jo.jvnGetObjectId() + " is registered");
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
        JvnCacheObject returnObject = nameCacheObject.get(jon);
        logger.info("Object required:" + jon + returnObject == null ? "NOT FOUND" : "FOUND");
        return returnObject == null ? null : returnObject.getObject();
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


        cacheObject.computeIfPresent(joi, new BiFunction<Integer, JvnCacheObject, JvnCacheObject>() {
            @Override
            public JvnCacheObject apply(Integer key, JvnCacheObject jvnCacheObject) {
                try {
                    switch (jvnCacheObject.getState()) {
                        case NL:
                            jvnCacheObject.getListClient().clear();
                            logger.info("IDObject: " + jvnCacheObject.getObject().jvnGetObjectId() + " State After: " + JvnState.NL.getValue() + "State before:" + JvnState.R.getValue());
                            jvnCacheObject.getListClient().add(js);
                            break;
                        case R:
                            logger.info("IDObject: " + jvnCacheObject.getObject().jvnGetObjectId() + " State After: " + JvnState.R.getValue() + "State before:" + JvnState.R.getValue());
                            jvnCacheObject.getListClient().add(js);
                            break;
                        case W:
                            JvnRemoteServer client = jvnCacheObject.getListClient().get(0);
                            jvnCacheObject.setLatesContent(client.jvnInvalidateWriterForReader(joi));
                            if (!client.equals(js)) {
                                jvnCacheObject.getListClient().add(js);
                            }
                            logger.info("IDObject: " + jvnCacheObject.getObject().jvnGetObjectId() + " State After: " + JvnState.W.getValue() + "State before:" + JvnState.R.getValue());
                            break;
                    }
                } catch (Exception e) {
                    logger.severe("Error in READ LOCK");
                }

                jvnCacheObject.setState(JvnState.R);

                return jvnCacheObject;
            }
        });

        JvnCacheObject objectCache = cacheObject.get(joi);

        if (objectCache == null) {

            logger.info("The ID: " + joi + " is not registered in the cache");
            throw new JvnException("The ID: " + joi + " is not registered in the cache");
        }

        return objectCache.getLatesContent();
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

        cacheObject.computeIfPresent(joi, new BiFunction<Integer, JvnCacheObject, JvnCacheObject>() {
            @Override
            public JvnCacheObject apply(Integer key, JvnCacheObject jvnCacheObject) {
                try {
                    switch (jvnCacheObject.getState()) {
                        case NL:
                            jvnCacheObject.setState(JvnState.W);
                            logger.info("IDObject: " + jvnCacheObject.getObject().jvnGetObjectId() + " State After: " + JvnState.NL.getValue() + "State before:" + JvnState.W.getValue());
                            break;
                        case R:
                            for (JvnRemoteServer s : jvnCacheObject.getListClient()) {
                                s.jvnInvalidateReader(joi);
                            }
                            jvnCacheObject.setState(JvnState.W);
                            logger.info("IDObject: " + jvnCacheObject.getObject().jvnGetObjectId() + " State After: " + JvnState.R.getValue() + "State before:" + JvnState.W.getValue());
                            //TODO We have to create the thread to wait the reponse of each client in te list. In V2
                            break;
                        case W:
                            jvnCacheObject.setLatesContent(jvnCacheObject.getListClient().get(0).jvnInvalidateWriter(joi));
                            logger.info("IDObject: " + jvnCacheObject.getObject().jvnGetObjectId() + " State After: " + JvnState.W.getValue() + "State before:" + JvnState.W.getValue());
                            break;
                    }
                    jvnCacheObject.getListClient().clear();
                    jvnCacheObject.getListClient().add(js);
                } catch (Exception e) {
                    logger.severe("Error in READ LOCK");
                }


                return jvnCacheObject;
            }
        });

        JvnCacheObject objectCache = cacheObject.get(joi);

        if (objectCache == null) {

            logger.info("The ID: " + joi + " is not registered in the cache");
            throw new JvnException("The ID: " + joi + " is not registered in the cache");
        }

        return objectCache.getLatesContent();

    }

    /**
     * A JVN server terminates
     *
     * @param js : the remote reference of the server
     * @throws java.rmi.RemoteException, JvnException
     **/
    public void jvnTerminate(JvnRemoteServer js)
            throws java.rmi.RemoteException, JvnException {
        Iterator<JvnCacheObject> iterator = cacheObject.values().iterator();
        JvnCacheObject client;

        logger.info("Trying to terminate a client...");

        while (iterator.hasNext()) {
            client = iterator.next();

            int indexClient;
            indexClient = client.getListClient().indexOf(js);
            if (indexClient > 0) {
                if (client.getState() == JvnState.W)
                    client.putLastContentAsCurrent();

                client.getListClient().remove(js);
                logger.info("A client has been terminated");

                if (client.getListClient().isEmpty()) client.setState(JvnState.NL);

                break;
            } else {
                logger.info("The client does not exist in the list");
            }


        }

    }
}

 

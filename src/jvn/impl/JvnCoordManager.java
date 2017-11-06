package jvn.impl;

import jvn.JvnObject;
import jvn.JvnRemoteCoord;
import jvn.JvnRemoteServer;
import jvn.exception.JvnException;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;

public class JvnCoordManager implements JvnRemoteCoord {

    JvnRemoteCoord jvnCoord;
    HashMap<Integer, JvnObject> map;
    JvnRemoteServer rs;
    boolean online = true;

    JvnCoordManager(HashMap<Integer, JvnObject> map, JvnRemoteServer js) throws RemoteException, NotBoundException, MalformedURLException {

        jvnCoord = (JvnRemoteCoord) Naming.lookup("Coordinator");
        this.map = map;
        rs = js;

    }

    @Override
    public int jvnGetObjectId() throws RemoteException, JvnException {
        int result = 2;
        if (online) {
            try {
                result = jvnCoord.jvnGetObjectId();
            } catch (ConnectException e) {
                online = false;

            }

        } else {
            //retryConnection();
            if (online) {
                result = jvnCoord.jvnGetObjectId();
            }
            result = 2;
        }

        return result;
    }

    @Override
    public void jvnRegisterObject(String jon, JvnObject jo, JvnRemoteServer js) throws RemoteException, JvnException {
        if (online) {
            try {
                jvnCoord.jvnRegisterObject(jon, jo, js);
            } catch (ConnectException e) {
                online = false;
            }
        } else {
            //retryConnection();
            if (online) {
                jvnCoord.jvnRegisterObject(jon, jo, js);
            }
        }
    }

    @Override
    public JvnObject jvnLookupObject(String jon, JvnRemoteServer js) throws RemoteException, JvnException {
        JvnObject result = null;
        if (online) {
            try {
                result = jvnCoord.jvnLookupObject(jon, js);
            } catch (ConnectException e) {
                online = false;
            }
        } else {
            //retryConnection();
            if (online) {
                result = jvnCoord.jvnLookupObject(jon, js);
            }


        }
        return result;
    }

    @Override
    public Serializable jvnLockRead(int joi, JvnRemoteServer js) throws RemoteException, JvnException {
        Serializable result = null;
        if (online) {
            try {
                result = jvnCoord.jvnLockRead(joi, js);
            } catch (ConnectException e) {
                online = false;
//                Serializable o = map.get(joi);
//                result = o;
            }

        } else {
            retryConnection(joi);
            if (online) {
                result = jvnCoord.jvnLockRead(joi, js);
            } else {
//                Serializable o = map.get(joi).jvnGetObjectState();
//                result = o;
            }


        }
        return result;
    }

    @Override
    public Serializable jvnLockWrite(int joi, JvnRemoteServer js) throws RemoteException, JvnException {
        Serializable result = null;
        if (online) {

            try {
                result = jvnCoord.jvnLockWrite(joi, js);
            } catch (ConnectException e) {
                online = false;
//                Serializable o = map.get(joi).jvnGetObjectState();
//                result = o;
            }
        } else {
            retryConnection(joi);
            if (online) {
                result = jvnCoord.jvnLockRead(joi, js);
            } else {
//                Serializable o = map.get(joi).jvnGetObjectState();
//                result = o;
            }


        }
        return result;
    }

    @Override
    public void jvnTerminate(JvnRemoteServer js) throws RemoteException, JvnException {
        if (online) {
            jvnCoord.jvnTerminate(js);
        }
    }

    public void setOnline(boolean a) {
        this.online = a;
    }

    public void retryConnection(int joi) {
        try {
            jvnCoord = (JvnRemoteCoord) Naming.lookup("Coordinator");

            Serializable o =  map.get(joi).jvnGetObjectState();

            JvnObject jvnO = jvnCoord.jvnLookupObject("IRC",rs);
            int id=0;
            if(jvnO==null){
                id = jvnCoord.jvnGetObjectId();
                JvnObject o1 = new JvnInterceptorImpl(id, o);
                jvnCoord.jvnRegisterObject("IRC", o1, rs);
            }else{
                id = ((JvnInterceptorImpl)jvnO).getId();
            }
            online = true;
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (JvnException e) {

        }
    }

}

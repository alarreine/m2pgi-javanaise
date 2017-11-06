package jvn.impl;

import jvn.JvnObject;
import jvn.JvnState;
import jvn.exception.JvnException;

import java.io.Serializable;



public class JvnInterceptorImpl implements JvnObject {

    JvnState state;
    private int id;
    private Serializable managedObject;



    public JvnInterceptorImpl(int id, Serializable managedObject) {
        this.id = id;
        this.managedObject = managedObject;
        this.state = JvnState.W;
    }

    @Override
    public void jvnLockRead() throws JvnException {

        // Accès séquentiel à l'objet
            boolean serverCall = false;
            synchronized (this) {
                if (this.state == JvnState.RC) {
                    //TODO VOIR CETTE PARTIE, J'ai ajoute pour obtenir la derniere version de l'object
                    this.state = JvnState.R;
                } else if (this.state == JvnState.R) {

                } else {
                    serverCall = true;
                }
            }
            if (serverCall) {
                Serializable o = JvnServerImpl.jvnGetServer().jvnLockRead(id);
                this.state = JvnState.R;
                synchronized (this) {
                    this.managedObject = o;
                }
            }
    }

    @Override
    public void jvnLockWrite() throws JvnException {
        // Accès séquentiel à l'objet

            boolean serverCall = false;
            synchronized (this) {
                if (this.state == JvnState.WC) {
                    this.state = JvnState.W;
                } else {
                    serverCall = true;
                }
            }
            if (serverCall) {
                Serializable o = JvnServerImpl.jvnGetServer().jvnLockWrite(id);
                this.state = JvnState.W;
                synchronized (this) {
                    this.managedObject = o;
                }
            }

    }

    @Override
    public void jvnUnLock() throws JvnException {
        synchronized (this) {
            if (this.state == JvnState.W) {
                this.state = JvnState.WC;
                this.notifyAll();
            } else if (this.state == JvnState.R) {
                this.state = JvnState.RC;
                this.notifyAll();
            } else {
                throw new JvnException("Unlock exception ");
            }


        }
    }

    @Override
    public int jvnGetObjectId() throws JvnException {
        return this.id;
    }

    @Override
    public Serializable jvnGetObjectState() throws JvnException {
        return this.managedObject;
    }

    @Override
    public void jvnInvalidateReader() throws JvnException {
        synchronized (this) {
            if (state == JvnState.R) {
                try {
                    while (state == JvnState.R) {
                        wait();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                state = JvnState.NL;
            }
        }
    }

    @Override
    public Serializable jvnInvalidateWriter() throws JvnException {
        synchronized (this) {
            if (state == JvnState.W) {
                try {
                    while (state == JvnState.W) {
                        this.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            state = JvnState.NL;
            return managedObject;
        }
    }

    @Override
    public Serializable jvnInvalidateWriterForReader() throws JvnException {
        synchronized (this) {
            switch (state) {
                case RWC:
                    state = JvnState.R;
                    break;
                case WC:
                    state = JvnState.NL;
                    break;
                case W:
                    while (state == JvnState.W) {
                        try {
                            while (state == JvnState.W) {
                                this.wait();
                            }

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
//TODO VOIR CETTE PARTIE DIAPO 7 et 8
                        state = JvnState.RC;
                    }
                    break;
                default:
                    break;
            }

            return managedObject;
        }
    }

    public String getStatus(){
        synchronized (this.state){
            return this.state.getValue();
        }
    }

}

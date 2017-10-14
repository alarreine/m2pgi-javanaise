package jvn.impl;

import jvn.JvnObject;
import jvn.JvnState;
import jvn.exception.JvnException;

import java.io.Serializable;
import java.util.concurrent.locks.ReentrantLock;

import static jvn.JvnState.*;

public class JvnInterceptorImpl implements JvnObject {

    JvnState state;
    private int id;
    private Serializable managedObject;

    private ReentrantLock lockState = new ReentrantLock();

    public JvnInterceptorImpl(int id, Serializable managedObject) {
        this.id = id;
        this.managedObject = managedObject;
        this.state = JvnState.W;
    }

    @Override
    public void jvnLockRead() throws JvnException {

        // Accès séquentiel à l'objet
        synchronized (this.state) {

            boolean serverCall = false;
            synchronized (this) {
                if (this.state == JvnState.RC) {
                    this.state = JvnState.R;
                } else if (this.state == JvnState.R) {

                } else {
                    serverCall = true;
                }
            }
            if (serverCall) {
                Serializable o = JvnServerImpl.jvnGetServer().jvnLockRead(id);
                this.state = R;
                synchronized (this) {
                    this.managedObject = o;
                }
            }
        }
    }

    @Override
    public void jvnLockWrite() throws JvnException {
        // Accès séquentiel à l'objet
        synchronized (this.state) {

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
                this.state = R;
                synchronized (this) {
                    this.managedObject = o;
                }
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
            if (state == R) {
                try {
                    while (state == R) {
                        wait();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                state = NL;
            }
        }
    }

    @Override
    public Serializable jvnInvalidateWriter() throws JvnException {
        synchronized (this) {
            if (state == W) {
                try {
                    while (state == W) {
                        this.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            state = NL;
            return managedObject;
        }
    }

    @Override
    public Serializable jvnInvalidateWriterForReader() throws JvnException {
        synchronized (this) {
            switch (state) {
                case RWC:
                    state = R;
                    break;
                case WC:
                    state = RC;
                    break;
                case W:
                    while (state == W) {
                        try {
                            while (state == W) {
                                this.wait();
                            }

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        state = RC;
                    }
                    break;
                default:
                    break;
            }

            return managedObject;
        }
    }

}

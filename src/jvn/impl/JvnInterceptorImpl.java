package jvn.impl;

import jvn.JvnObject;
import jvn.JvnState;
import jvn.exception.JvnException;

import java.io.Serializable;
import java.util.concurrent.locks.ReentrantLock;

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
                this.state = JvnState.R;

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
                } else if (this.state == JvnState.W) {

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
    }

    @Override
    public void jvnUnLock() throws JvnException {
        synchronized (this) {
            if (this.state == JvnState.W) {
                this.state = JvnState.WC;
                this.notifyAll();
            } else if (this.state == JvnState.R) {
                this.state = JvnState.WC;
                this.notifyAll();
            } else {

                throw new JvnException("We have an exception in UnLock: ");
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
        // TODO InvalidateReader

    }

    @Override
    public Serializable jvnInvalidateWriter() throws JvnException {
        // TODO InvalidateWriter
        return null;
    }

    @Override
    public Serializable jvnInvalidateWriterForReader() throws JvnException {
        // TODO InvalidateWriterForReader
        return null;
    }

}

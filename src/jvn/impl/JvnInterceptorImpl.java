package jvn.impl;

import jvn.JvnObject;
import jvn.JvnState;
import jvn.exception.JvnException;

import java.io.Serializable;
import java.util.concurrent.locks.ReentrantLock;

import static jvn.JvnState.*;

public class JvnInterceptorImpl implements JvnObject {

	private JvnState state;
	private int id;
	private Serializable managedObject;

	private ReentrantLock lockState = new ReentrantLock();

	public JvnInterceptorImpl(JvnState state, int id, Serializable managedObject) {
		this.state = state;
		this.id = id;
		this.managedObject = managedObject;
	}

	@Override
	public void jvnLockRead() throws JvnException {
		lockState.lock();
		try{
			switch (this.state){
				case RC:
					this.state=R;
					break;
				case NL:
					this.state=R;
					break;
				case WC:
					this.state=RWC;
					break;
				case R:
					this.state=R;
				case W:
					this.state=R;
				case RWC:
					this.state=RWC;
			}
		}catch (Exception e){
			throw new JvnException("We have an exception in LockRead: "+e.getMessage());

		}finally {
			lockState.unlock();
		}

			//TODO call to jvnLockRead from Server



	}

	@Override
	public void jvnLockWrite() throws JvnException {
		lockState.lock();
		try {
			switch (this.state){
				case NL:
					this.state=W;
					break;
				case RC:
					this.state=W;
					break;
				case WC:
					this.state=RWC;
					break;
				case R:
					this.state=W;
					break;
				case W:
					this.state=W;
					break;
				case RWC:
					this.state=W;
					break;
			}
		}catch (Exception e){
			throw new JvnException("We have an exception in LockWrite: "+e.getMessage());
		}finally {
			lockState.unlock();
		}

		//TODO call to jvnLockWrite from Server
	}

	@Override
	public void jvnUnLock() throws JvnException {
		lockState.lock();
		try {
			switch (this.state){
				case R:
					this.state=RC;
					break;
				case W:
					this.state=WC;
					break;
				case RWC:
					this.state=WC;
					break;
			}
		}catch (Exception e){
			throw new JvnException("We have an exception in UnLock: "+e.getMessage());
		}finally {
			lockState.unlock();
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

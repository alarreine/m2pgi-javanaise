package jvn;

import java.io.Serializable;

public class JvnInterceptorImpl implements JvnObject {

	private JvnState state;
	private int id;
	private JvnObject managedObject;

	@Override
	public void jvnLockRead() throws JvnException {
		state = JvnState.R;

	}

	@Override
	public void jvnLockWrite() throws JvnException {
		state = JvnState.W;

	}

	@Override
	public void jvnUnLock() throws JvnException {
		state = JvnState.NL;

	}

	@Override
	public int jvnGetObjectId() throws JvnException {

		return this.id;
	}

	@Override
	public Serializable jvnGetObjectState() throws JvnException {
		return this.state.getNumber();
	}

	@Override
	public void jvnInvalidateReader() throws JvnException {
		// TODO Auto-generated method stub

	}

	@Override
	public Serializable jvnInvalidateWriter() throws JvnException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Serializable jvnInvalidateWriterForReader() throws JvnException {
		// TODO Auto-generated method stub
		return null;
	}

}

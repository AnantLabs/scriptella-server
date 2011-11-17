package com.scriptella.server.core.job;

import java.io.Writer;
import java.util.concurrent.Callable;

/**
 * This class defines an API for a job runner. TODO Add a factory for
 * instantiation and configuration of {@link AbstractJobRunner}.
 * 
 * @author <a href="mailto:scriptella@gmail.com">Fyodor Kupolov</a>
 * 
 */
public abstract class AbstractJobRunner<R> implements Callable<R> {
	private Writer out;
	private JobExecutionCallback executionCallback;

	public void setExecutionCallback(JobExecutionCallback executionCallback) {
		this.executionCallback = executionCallback;
	}

	public void setOut(Writer out) {
		this.out = out;
	}

	protected JobExecutionCallback getExecutionCallback() {
		return executionCallback;
	}

	protected Writer getOut() {
		return out;
	}

	public abstract void cancel();
}

package com.scriptella.server.core.job;

import com.scriptella.server.core.job.JobExecutionCallback.CompletionStatus;

public abstract class SimpleJobRunner<R> extends AbstractJobRunner<R> {
	private Thread currentThread;
	private boolean interrupted;

	@Override
	public void cancel() {
		if (currentThread != null) {
			// store a flag in a field in order to ignore clearing of
			// "interrupted" flag in the currentThread
			interrupted = true;
			currentThread.interrupt();
		}

	}

	@Override
	public R call() throws Exception {
		if (getExecutionCallback() == null) {
			throw new IllegalStateException("Execution callback must be set before calling this method");
		}
		currentThread = Thread.currentThread();
		try {
			R r = execute();
			getExecutionCallback().jobCompleted(interrupted ? CompletionStatus.CANCELLED : CompletionStatus.SUCCESSFUL);
			return r;
		} catch (Exception e) {
			getExecutionCallback().jobCompleted(interrupted ? CompletionStatus.CANCELLED : CompletionStatus.FAILED);
			throw e;
		}

	}

	protected abstract R execute() throws Exception;
}

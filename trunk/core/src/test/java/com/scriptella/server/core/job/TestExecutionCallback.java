package com.scriptella.server.core.job;

import java.util.ArrayList;
import java.util.List;

public class TestExecutionCallback implements JobExecutionCallback {
	public List<JobMessage> messages = new ArrayList<JobMessage>();
	public double progress;
	public CompletionStatus status;

	@Override
	public void logMessage(JobMessage msg) {
		messages.add(msg);
		
	}

	@Override
	public void setJobProgress(double percentage) {
		progress=percentage;
		
	}

	@Override
	public void jobCompleted(CompletionStatus status) {
		this.status = status;
	}

}

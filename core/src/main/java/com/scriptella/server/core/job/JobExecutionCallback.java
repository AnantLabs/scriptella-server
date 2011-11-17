package com.scriptella.server.core.job;

public interface JobExecutionCallback {
	void logMessage(JobMessage msg);
	void setJobProgress(double percentage);
	void jobCompleted(CompletionStatus status);
	
	enum CompletionStatus {
		SUCCESSFUL, FAILED, CANCELLED
	}
}

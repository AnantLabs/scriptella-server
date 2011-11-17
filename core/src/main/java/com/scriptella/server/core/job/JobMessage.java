package com.scriptella.server.core.job;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

public class JobMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Throwable[] NO_THROWABLES = new Throwable[0];
	private static final Map<String, Serializable> NO_RELATED_OBJECTS = Collections.emptyMap();
	private Level level;
	private String message;
	private Throwable[] errors;
	private Map<String, Serializable> relatedObjects;

	public JobMessage(Level level, String message, Map<String, Serializable> relatedObjects, Throwable... errors) {
		this.level = level;
		this.message = message;
		this.errors = errors;
		this.relatedObjects = relatedObjects;
	}

	public JobMessage(Level level, String message) {
		this.level = level;
		this.message = message;
		this.errors = NO_THROWABLES;
		this.relatedObjects = NO_RELATED_OBJECTS;
	}
	
	public Level getLevel() {
		return level;
	}

	public String getMessage() {
		return message;
	}
	public Throwable[] getErrors() {
		return errors;
	}

	public Map<String, Serializable> getRelatedObjects() {
		return relatedObjects;
	}

	public static enum Level {
		DEBUG(10), INFO(20), PROGRESS(30), WARNING(40), ERROR(50);
		int level;

		private Level(int level) {
			this.level = level;
		}
	}

}

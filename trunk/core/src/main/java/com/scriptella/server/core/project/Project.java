package com.scriptella.server.core.project;

import com.scriptella.server.core.io.FileRef;
import com.scriptella.server.project.model.XmlProject;

import java.io.Serializable;

public class Project implements Serializable {
	private static final long serialVersionUID = 1L;
	private FileRef projectFile;
	private XmlProject project;
	private boolean dirty;

	public XmlProject getModel() {
		return project;
	}
	
	public FileRef getProjectFile() {
		return projectFile;
	}

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
	
	
	
}

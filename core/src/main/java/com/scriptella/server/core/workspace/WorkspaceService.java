package com.scriptella.server.core.workspace;

import com.scriptella.server.core.project.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class WorkspaceService {
	private static final Logger LOG = LoggerFactory.getLogger(WorkspaceService.class);
	private Map<String, Project> projectsMap;
	
	
	public Collection<Project> getProjects() {
		return Collections.unmodifiableCollection(projectsMap.values());
	}
	
	
	
	public void save(Project project) {
		
	}
	
	
}

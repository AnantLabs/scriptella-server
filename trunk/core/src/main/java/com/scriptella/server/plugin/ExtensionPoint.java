package com.scriptella.server.plugin;

public interface ExtensionPoint {
	Class<?> getInterface();
	String getName();
	String getDescription();
}

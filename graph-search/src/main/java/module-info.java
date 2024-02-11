module de.amr.graph.pathfinder {

	requires transitive de.amr.graph.core;
	requires transitive org.tinylog.api;

	exports de.amr.graph.pathfinder.api;
	exports de.amr.graph.pathfinder.impl;
	exports de.amr.graph.pathfinder.util;
}
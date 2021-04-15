module de.amr.graph.grid {

	requires transitive de.amr.graph.core;
	requires transitive de.amr.graph.pathfinder;
	
	exports de.amr.graph.grid.api;
	exports de.amr.graph.grid.curves;
	exports de.amr.graph.grid.impl;
	exports de.amr.graph.grid.iterators;
	exports de.amr.graph.grid.shapes;
	exports de.amr.graph.grid.traversals;

}
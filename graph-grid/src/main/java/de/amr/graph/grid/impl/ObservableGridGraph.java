package de.amr.graph.grid.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.IntFunction;

import de.amr.graph.core.api.Edge;
import de.amr.graph.core.api.ObservableGraph;
import de.amr.graph.event.EdgeEvent;
import de.amr.graph.event.GraphObserver;
import de.amr.graph.event.VertexEvent;
import de.amr.graph.grid.api.GridTopology;
import de.amr.graph.grid.api.ObservableGridGraph2D;

/**
 * A grid graph which can be observed.
 * 
 * @author Armin Reichert
 * 
 * @param <V> vertex label type
 * @param <E> edge label type
 */
public class ObservableGridGraph<V, E> extends GridGraph<V, E> implements ObservableGridGraph2D<V, E> {

	private final Set<GraphObserver<V, E>> observers;
	private boolean eventsEnabled;

	public ObservableGridGraph(int numCols, int numRows, GridTopology top, IntFunction<V> fnDefaultVertexLabel,
			BiFunction<Integer, Integer, E> fnDefaultEdgeLabel, BiFunction<Integer, Integer, Edge> fnEdgeFactory) {
		super(numCols, numRows, top, fnDefaultVertexLabel, fnDefaultEdgeLabel, fnEdgeFactory);
		observers = new HashSet<>();
		eventsEnabled = true;
	}

	@Override
	public void clearVertexLabels() {
		super.clearVertexLabels();
		fireGraphChange(this);
	}

	@Override
	public void setDefaultVertexLabel(IntFunction<V> fnDefaultLabel) {
		super.setDefaultVertexLabel(fnDefaultLabel);
		fireGraphChange(this);
	}

	@Override
	public void set(int v, V vertex) {
		super.set(v, vertex);
		fireVertexChange(v);
	}

	@Override
	public void addEdge(int u, int v) {
		super.addEdge(u, v);
		fireEdgeAdded(u, v);
	}

	@Override
	public void removeEdge(int u, int v) {
		edge(u, v).ifPresent(edge -> {
			super.removeEdge(u, v);
			fireEdgeRemoved(u, v);
		});
	}

	@Override
	public void removeEdges() {
		super.removeEdges();
		fireGraphChange(this);
	}

	@Override
	public void fill() {
		super.fill();
		fireGraphChange(this);
	}

	/* {@link ObservableGraph} interface */

	@Override
	public void addGraphObserver(GraphObserver<V, E> obs) {
		observers.add(obs);
	}

	@Override
	public void removeGraphObserver(GraphObserver<V, E> obs) {
		observers.remove(obs);
	}

	@Override
	public void setEventsEnabled(boolean enabled) {
		eventsEnabled = enabled;
	}

	// helper methods

	protected void fireVertexChange(int v) {
		if (eventsEnabled) {
			observers.forEach(o -> o.vertexChanged(new VertexEvent<>(this, v)));
		}
	}

	protected void fireEdgeAdded(int u, int v) {
		if (eventsEnabled) {
			observers.forEach(o -> o.edgeAdded(new EdgeEvent<>(this, u, v)));
		}
	}

	protected void fireEdgeChanged(int u, int v) {
		if (eventsEnabled) {
			observers.forEach(o -> o.edgeChanged(new EdgeEvent<>(this, u, v)));
		}
	}

	protected void fireEdgeRemoved(int u, int v) {
		if (eventsEnabled) {
			observers.forEach(o -> o.edgeRemoved(new EdgeEvent<>(this, u, v)));
		}
	}

	protected void fireGraphChange(ObservableGraph<V, E> graph) {
		if (eventsEnabled) {
			observers.forEach(o -> o.graphChanged(graph));
		}
	}
}
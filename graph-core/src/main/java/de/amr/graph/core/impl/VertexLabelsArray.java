package de.amr.graph.core.impl;

import java.util.function.Function;

import de.amr.graph.core.api.VertexLabeling;

/**
 * Vertex labels implementation for dense graphs.
 * 
 * @author Armin Reichert
 *
 * @param <V>
 *          vertex label type
 */
public class VertexLabelsArray<V> implements VertexLabeling<V> {

	private Object[] labels;
	private Function<Integer, V> fnDefaultLabel;

	public VertexLabelsArray(int size) {
		labels = new Object[size];
	}

	@SuppressWarnings("unchecked")
	@Override
	public V get(int v) {
		return (V) (labels[v] != null ? labels[v] : fnDefaultLabel);
	}

	@Override
	public void set(int v, V vertex) {
		labels[v] = vertex;
	}

	@Override
	public void clearVertexLabels() {
		labels = new Object[labels.length];
	}

	@Override
	public V getDefaultVertexLabel(int v) {
		return fnDefaultLabel.apply(v);
	}

	@Override
	public void setDefaultVertexLabel(Function<Integer, V> fnDefaultLabel) {
		this.fnDefaultLabel = fnDefaultLabel;
	}
}
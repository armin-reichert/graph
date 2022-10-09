package de.amr.graph.core.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.function.IntFunction;

import de.amr.graph.core.api.VertexLabeling;

/**
 * Vertex labels implementation for sparse graphs.
 * 
 * @author Armin Reichert
 *
 * @param <V> vertex label type
 */
public class VertexLabelsMap<V> implements VertexLabeling<V> {

	private final Map<Integer, V> labels = new HashMap<>();
	private IntFunction<V> fnDefaultLabel;

	public VertexLabelsMap(IntFunction<V> defaultLabel) {
		this.fnDefaultLabel = defaultLabel;
	}

	@Override
	public V get(int v) {
		return labels.containsKey(v) ? labels.get(v) : fnDefaultLabel.apply(v);
	}

	@Override
	public void set(int v, V vertexLabel) {
		labels.put(v, vertexLabel);
	}

	@Override
	public void clearVertexLabels() {
		labels.clear();
	}

	@Override
	public V getDefaultVertexLabel(int v) {
		return fnDefaultLabel.apply(v);
	}

	@Override
	public void setDefaultVertexLabel(IntFunction<V> fnDefaultLabel) {
		this.fnDefaultLabel = fnDefaultLabel;
	}

	@Override
	public String toString() {
		return new StringBuilder().append("Labels (default='").append(fnDefaultLabel.apply(0)).append("'):\n")
				.append(labels).toString();
	}
}
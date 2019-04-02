package de.amr.graph.core.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import de.amr.datastruct.TwoSet;
import de.amr.graph.core.api.EdgeLabeling;

public class EdgeLabelsMap<E> implements EdgeLabeling<E> {

	private BiFunction<Integer, Integer, E> fnDefaultLabel;

	private Map<TwoSet<Integer>, E> labels = new HashMap<>();

	public EdgeLabelsMap(BiFunction<Integer, Integer, E> fnDefaultLabel) {
		this.fnDefaultLabel = fnDefaultLabel;
	}

	@Override
	public E getEdgeLabel(int u, int v) {
		TwoSet<Integer> edge = TwoSet.of(u, v);
		return labels.containsKey(edge) ? labels.get(edge) : fnDefaultLabel.apply(u, v);
	}

	@Override
	public void setEdgeLabel(int u, int v, E e) {
		labels.put(TwoSet.of(u, v), e);
	}

	@Override
	public void clearEdgeLabels() {
		labels.clear();
	}

	@Override
	public void setDefaultEdgeLabel(BiFunction<Integer, Integer, E> fnDefaultLabel) {
		this.fnDefaultLabel = fnDefaultLabel;
	}

	@Override
	public E getDefaultEdgeLabel(int u, int v) {
		return fnDefaultLabel.apply(u, v);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Default label: ").append(fnDefaultLabel.apply(0, 0)).append("\n");
		sb.append("Labels:\n").append(labels);
		return sb.toString();
	}
}
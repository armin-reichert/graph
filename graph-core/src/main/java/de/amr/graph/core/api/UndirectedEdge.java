package de.amr.graph.core.api;

/**
 * Undirected edge implementation. Two undirected edges are equal if their vertex sets are equal.
 * 
 * @author Armin Reichert
 */
public record UndirectedEdge(int either, int other) implements Edge {
}
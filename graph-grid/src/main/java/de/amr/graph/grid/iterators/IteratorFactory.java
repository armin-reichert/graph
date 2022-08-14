package de.amr.graph.grid.iterators;

import java.util.Collection;
import java.util.Iterator;

public interface IteratorFactory {

	@SafeVarargs
	public static <C> Iterator<C> sequence(Iterator<C>... sources) {
		return new SequentialIterator<>(sources);
	}

	@SuppressWarnings("unchecked")
	public static <C> Iterator<C> seq(Collection<Iterator<C>> sources) {
		Iterator<?>[] sourcesArray = new Iterator[sources.size()];
		sourcesArray = sources.toArray(sourcesArray);
		return sequence((Iterator<C>[]) sourcesArray);
	}

	@SafeVarargs
	public static <C> Iterator<C> parallel(Iterator<C>... sources) {
		return new ParallelIterator<>(sources);
	}

	@SuppressWarnings("unchecked")
	public static <C> Iterator<C> par(Collection<Iterator<C>> sources) {
		Iterator<?>[] sourcesArray = new Iterator[sources.size()];
		sourcesArray = sources.toArray(sourcesArray);
		return parallel((Iterator<C>[]) sourcesArray);
	}
}
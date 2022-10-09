package de.amr.datastruct;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

/**
 * A constant 2-element set (unordered tuple).
 * 
 * @author Armin Reichert
 *
 * @param <T> type of elements
 */
public class TwoSet<T> implements Set<T> {

	public static <T> TwoSet<T> of(T e1, T e2) {
		return new TwoSet<>(e1, e2);
	}

	public final T e1;
	public final T e2;

	public TwoSet(T e1, T e2) {
		this.e1 = Objects.requireNonNull(e1);
		this.e2 = Objects.requireNonNull(e2);
		if (e1.equals(e2)) {
			throw new IllegalArgumentException("Set elements must be different");
		}
	}

	@Override
	public Iterator<T> iterator() {
		return Arrays.asList(e1, e2).iterator();
	}

	@Override
	public int size() {
		return 2;
	}

	@Override
	public int hashCode() {
		// ensure that equal sets have the same hash code
		return Objects.hash(e1, e2) + Objects.hash(e2, e1);
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof TwoSet)) { // includes case: other == null
			return false;
		}
		TwoSet<?> otherSet = (TwoSet<?>) other;
		// ensure that set(e1, e2) equals set(e2, e1)
		return e1.equals(otherSet.e1) && e2.equals(otherSet.e2) || e1.equals(otherSet.e2) && e2.equals(otherSet.e1);
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean contains(Object element) {
		return e1.equals(element) || e2.equals(element);
	}

	@Override
	public Object[] toArray() {
		return new Object[] { e1, e2 };
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T1> T1[] toArray(T1[] a) {
		a[0] = (T1) e1;
		a[1] = (T1) e2;
		return a;
	}

	@Override
	public boolean add(T e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}
}
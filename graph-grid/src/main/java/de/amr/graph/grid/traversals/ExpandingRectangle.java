package de.amr.graph.grid.traversals;

import java.util.Iterator;

import de.amr.graph.grid.shapes.Rectangle;

/**
 * A sequence of cells filling the grid like an expanding rectangle with left-upper corner at the
 * left-upper grid corner.
 * 
 * @author Armin Reichert
 */
public class ExpandingRectangle implements Iterable<Integer> {

	private final Rectangle startRect;
	private boolean expandHoriz;
	private boolean expandVert;
	private int maxExpansion;
	private int expansionRate;

	public ExpandingRectangle(Rectangle startRect) {
		this.startRect = startRect;
		this.expandHoriz = true;
		this.expandVert = true;
		this.maxExpansion = 0;
		this.expansionRate = 1;
	}

	public void setMaxExpansion(int maxExpansion) {
		this.maxExpansion = maxExpansion;
	}

	public void setExpandHorizontally(boolean expandHoriz) {
		this.expandHoriz = expandHoriz;
	}

	public void setExpandVertically(boolean expandVert) {
		this.expandVert = expandVert;
	}

	public void setExpansionRate(int expansionRate) {
		this.expansionRate = expansionRate;
	}

	@Override
	public Iterator<Integer> iterator() {
		return new Iterator<Integer>() {

			private Rectangle currentRect = startRect;
			private Iterator<Integer> iterator = currentRect.iterator();
			private int expansion;

			@Override
			public boolean hasNext() {
				return expansion < maxExpansion || iterator.hasNext();
			}

			@Override
			public Integer next() {
				if (!iterator.hasNext()) {
					int width = currentRect.getWidth() + (expandHoriz ? expansionRate : 0);
					int height = currentRect.getHeight() + (expandVert ? expansionRate : 0);
					expansion += expansionRate;
					currentRect = new Rectangle(currentRect.grid, currentRect.getLeftUpperCorner(), width,
							height);
					iterator = currentRect.iterator();
				}
				return iterator.next();
			}
		};
	}
}

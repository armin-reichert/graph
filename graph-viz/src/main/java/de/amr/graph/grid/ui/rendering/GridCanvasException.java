package de.amr.graph.grid.ui.rendering;

public class GridCanvasException extends RuntimeException {

	public GridCanvasException() {
	}

	public GridCanvasException(String message) {
		super(message);
	}

	public GridCanvasException(Throwable cause) {
		super(cause);
	}

	public GridCanvasException(String message, Throwable cause) {
		super(message, cause);
	}

	public GridCanvasException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}

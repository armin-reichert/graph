module de.amr.graph.viz {

    requires de.amr.graph.core;
    requires de.amr.graph.grid;
    requires de.amr.graph.pathfinder;

    exports de.amr.graph.grid.ui;
    exports de.amr.graph.grid.ui.animation;
    exports de.amr.graph.grid.ui.rendering;
    exports de.amr.swing;
}
# Graph data structures and algorithms.

This library has been written as the foundation of my [maze generation library](https://github.com/armin-reichert/mazes). 

<img width="640" src="https://github.com/armin-reichert/mazes/blob/master/MazeAlgorithms/images/gen/maze_80x60_WilsonUSTRecursiveCrosses.gif"/>

The library contains a space-efficient implementation for 2D grid graphs and several pathfinder implementations (BFS, DFS, Hill-Climbing, Best-First Search, A*, Dijkstra). 

I tried to achieve "text book quality" in the code. See for example the following implementations:

*** Depth-First Search:

```java
public class DepthFirstSearch<V, E> extends GraphSearch<V, E, LIFO_VertexQueue> {

	public DepthFirstSearch(Graph<V, E> graph) {
		super(graph);
		frontier = new LIFO_VertexQueue();
	}
}
```

*** Breadth-First Search:

```java
public class BreadthFirstSearch<V, E> extends GraphSearch<V, E, FIFO_VertexQueue> {

	public BreadthFirstSearch(Graph<V, E> graph, ToDoubleBiFunction<Integer, Integer> fnEdgeCost) {
		super(graph, fnEdgeCost);
		frontier = new FIFO_VertexQueue();
	}

	public BreadthFirstSearch(Graph<V, E> graph) {
		this(graph, (u, v) -> 1);
	}
}
```

*** Uniform-Cost Search (Dijkstra):

```java
public class DijkstraSearch<V, E> extends AStarSearch<V, E> {

	public DijkstraSearch(Graph<V, E> graph, ToDoubleFunction<E> fnEdgeCost) {
		super(graph, fnEdgeCost, (u, v) -> 0);
	}
}
```

*** Greedy Best-First Search:

```java
public class BestFirstSearch<V, E> extends GraphSearch<V, E, MinPQ_VertexQueue> {

	public BestFirstSearch(Graph<V, E> graph, ToDoubleFunction<Integer> fnVertexPriority) {
		this(graph, fnVertexPriority, (u, v) -> 1);
	}

	public BestFirstSearch(Graph<V, E> graph, ToDoubleFunction<Integer> fnVertexPriority,
		ToDoubleBiFunction<Integer, Integer> fnEdgeCost) {
		super(graph, fnEdgeCost);
		frontier = new MinPQ_VertexQueue(fnVertexPriority);
	}
}
```

There is a demo application included where you can add/remove walls in a grid and run different pathfinding algorithms:

![Path finding demo application](https://github.com/armin-reichert/graph/blob/master/PathFinderDemo/doc/pathfinder.png)

![Path finding demo application](https://github.com/armin-reichert/graph/blob/master/PathFinderDemo/doc/astar.png)


Additional sample apps show graph based implementations of space filling curves (Hilbert, Peano, Moore curves).

![Hilbert curve](https://github.com/armin-reichert/graph/blob/master/SpaceFillingCurves/doc/hilbert.png)

This library and path finding is also used by my [Pac-Man game](https://github.com/armin-reichert/pacman) implementation
to verify its reusability.

![Pac-Man](https://github.com/armin-reichert/pacman/blob/master/doc/pacman-pathfinding.png)

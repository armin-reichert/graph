# Graph data structures and algorithms.

This library has been written as the foundation of my [maze generation library](https://github.com/armin-reichert/mazes). 

<img width="640" src="https://github.com/armin-reichert/mazes/wiki/images/gen/maze_80x60_WilsonUSTRecursiveCrosses.gif"/>

The library contains a space-efficient implementation for 2D grid graphs and several pathfinder implementations (BFS, DFS, Hill-Climbing, Best-First Search, A*, Dijkstra). 

I tried to achieve "text book quality" in the code. See for example the following implementations:

### Depth-First Search:

```java
public class DepthFirstSearch extends AbstractGraphSearch<LIFO_VertexQueue> {

	public DepthFirstSearch(Graph<?, ?> graph) {
		super(graph, new LIFO_VertexQueue());
	}
}
```

### Breadth-First Search:

```java
public class BreadthFirstSearch extends AbstractGraphSearch<FIFO_VertexQueue> {

	public BreadthFirstSearch(Graph<?, ?> graph) {
		super(graph, (u, v) -> 1, new FIFO_VertexQueue());
	}

	public BreadthFirstSearch(Graph<?, ?> graph, ToDoubleBiFunction<Integer, Integer> fnEdgeCost) {
		super(graph, fnEdgeCost, new FIFO_VertexQueue());
	}
}
```

### Uniform-Cost Search (Dijkstra):

```java
public class DijkstraSearch extends AStarSearch {

	public DijkstraSearch(Graph<?, ?> graph, ToDoubleBiFunction<Integer, Integer> fnEdgeCost) {
		super(graph, fnEdgeCost, (u, v) -> 0);
	}
}
```

### Greedy Best-First Search:

```java
public class BestFirstSearch extends AbstractGraphSearch<MinPQ_VertexQueue> {

	public BestFirstSearch(Graph<?, ?> graph, ToDoubleFunction<Integer> fnVertexPriority) {
		super(graph, (u, v) -> 1, new MinPQ_VertexQueue(fnVertexPriority));
	}

	public BestFirstSearch(Graph<?, ?> graph, ToDoubleFunction<Integer> fnVertexPriority,
			ToDoubleBiFunction<Integer, Integer> fnEdgeCost) {
		super(graph, fnEdgeCost, new MinPQ_VertexQueue(fnVertexPriority));
	}
}
```

The graph library and path finding is also used in my [Pac-Man game](https://github.com/armin-reichert/pacman) implementation:

![Pac-Man](https://github.com/armin-reichert/pacman/blob/master/doc/pacman-pathfinding.png)

# graph-search

The library contains several graph search (path finder) implementations (BFS, DFS, Hill-Climbing, Best-First Search, A*, Dijkstra).

![Path Finder Demo](doc/demoapp.png "Path Finding Demo")


I tried to achieve "text book quality" in the code. See for example the following implementations:

### Depth-First Search:

```java
public class DepthFirstSearch extends AbstractGraphSearch {

	public DepthFirstSearch(Graph<?, ?> graph) {
		super(graph);
		frontier = new LIFOVertexQueue();
	}
}
```

### Breadth-First Search:

```java
public class BreadthFirstSearch extends AbstractGraphSearch {

	public BreadthFirstSearch(Graph<?, ?> graph) {
		this(graph, (u, v) -> 1);
	}

	public BreadthFirstSearch(Graph<?, ?> graph, ToDoubleBiFunction<Integer, Integer> fnEdgeCost) {
		super(graph);
		this.frontier = new FIFOVertexQueue();
		this.fnEdgeCost = Objects.requireNonNull(fnEdgeCost);
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

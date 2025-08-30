package de.amr.graph.test;


import java.util.HashMap;
import java.util.Map;


import de.amr.datastruct.TwoSet;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TwoSetTest {

	@Test
	public void testEquals() {
		TwoSet<Integer> one = TwoSet.of(1, 2);
		TwoSet<Integer> two = TwoSet.of(2, 1);
		TwoSet<Integer> three = TwoSet.of(1, 3);
		assertEquals(one, two);
		assertNotEquals(one, three);
	}

	@Test
	public void testMapAccess() {
		Map<TwoSet<Integer>, String> map = new HashMap<>();
		TwoSet<Integer> one = TwoSet.of(1, 2);
		TwoSet<Integer> two = TwoSet.of(2, 1);
		assertEquals(one, two);
		map.put(one, "A");
		assertEquals(map.get(one), map.get(two));
	}
}
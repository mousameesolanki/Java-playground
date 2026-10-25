public class HashMap {
    //Collections: HashMap internals
//"Walk through how HashMap resolves collisions and why Java 8 changed bucket structure."

// Java 8+: bucket is a linked list until 8 entries, then converts to a Red-Black tree
// (treeification) for O(log n) worst-case instead of O(n) linked-list scan.

//HashMap<String, Integer> map = new HashMap<>();
//map.put("a", 1);
// 1. hash("a") computed, spread with (h = key.hashCode()) ^ (h >>> 16)
// 2. bucket index = hash & (capacity - 1)
// 3. if bucket occupied -> equals() check against existing entries -> append or treeify

// Load factor 0.75 -> resize (double capacity) when size > threshold
// ConcurrentHashMap (Java 8+): no segment locking, uses CAS + synchronized per-bin
}

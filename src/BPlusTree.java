
/**
 * Implement B+ tree
 *
 * @param <T> specifies the value type
 * @param <V> uses generics, specifies the index type, and specifies that you
 *            must inherit Comparable
 */
public class BPlusTree<T, V extends Comparable<V>> {
	// B+ tree order
	private Integer bTreeOrder;
	// The number of children owned by the non-leaf node of the B+ tree (also the
	// minimum number of keys)
	// private Integer minNUmber;
	// The number of nodes that the non-leaf node of the B+ tree has at its maximum
	// (and also the maximum number of keys)
	private Integer maxNumber;

	private Node<T, V> root;

	private LeafNode<T, V> left;

	// No parameter construction method, the default order is 3
	public BPlusTree() {
		this(3);
	}

	// There is a construction method, you can set the order of the B + tree
	public BPlusTree(Integer bTreeOrder) {
		this.bTreeOrder = bTreeOrder;
		// this.minNUmber = (int) Math.ceil(1.0 * bTreeOrder / 2.0);
		// Because there may be more than the upper limit during the insertion of the
		// node, so add 1 here
		this.maxNumber = bTreeOrder + 1;
		this.root = new LeafNode<T, V>();
		this.left = null;
	}

	// Inquire
	public T find(V key) {
		T t = this.root.find(key);
		if (t == null) {
			System.out.println("does not exist");
		}
		return t;
	}

	// insert
	public void insert(T value, V key) {
		if (key == null)
			return;
		Node<T, V> t = this.root.insert(value, key);
		if (t != null)
			this.root = t;
		this.left = (LeafNode<T, V>) this.root.refreshLeft();

		System.out.println("Insert completed, the current root node is:");
		for (int j = 0; j < this.root.number; j++) {
			System.out.print((V) this.root.keys[j] + " ");
		}
		System.out.println();
	}

	
	
	
	
	
	/**
	 * Node parent class, because in the B+ tree, non-leaf nodes do not need to
	 * store specific data, just need to use the index as a key. So the leaves and
	 * non-leaf nodes are not the same, but they share some methods, so use the Node
	 * class as the parent class. And because you want to call some public methods
	 * to each other, use abstract classes
	 *
	 * @param <T> with BPlusTree
	 * @param <V>
	 */
	abstract class Node<T, V extends Comparable<V>> {
		// parent node
		protected Node<T, V> parent;
		// child node
		protected Node<T, V>[] childs;
		// number of keys (child nodes)
		protected Integer number;
		// key
		protected Object keys[];

		// Construction method
		public Node() {
			this.keys = new Object[maxNumber];
			this.childs = new Node[maxNumber];
			this.number = 0;
			this.parent = null;
		}

		// lookup
		abstract T find(V key);

		// insert
		abstract Node<T, V> insert(T value, V key);

		abstract LeafNode<T, V> refreshLeft();
	}

	
	
	
	
	/**
	 * Non-leaf node class
	 * 
	 * @param <T>
	 * @param <V>
	 */

	class BPlusNode<T, V extends Comparable<V>> extends Node<T, V> {

		public BPlusNode() {
			super();
		}

		/**
		 * Recursive lookup, here is just to determine exactly which value the value is
		 * in, the real find the leaf node will check
		 * 
		 * @param key
		 * @return
		 */
		@Override
		T find(V key) {
			int i = 0;
			while (i < this.number) {
				if (key.compareTo((V) this.keys[i]) <= 0)
					break;
				i++;
			}
			if (this.number == i)
				return null;
			return this.childs[i].find(key);
		}

		/**
		 * Recursive insertion, first insert the value into the corresponding leaf node,
		 * and finally call the insert class of the leaf node
		 * 
		 * @param value
		 * @param key
		 */
		@Override
		Node<T, V> insert(T value, V key) {
			int i = 0;
			while (i < this.number) {
				if (key.compareTo((V) this.keys[i]) < 0)
					break;
				i++;
			}
			if (key.compareTo((V) this.keys[this.number - 1]) >= 0) {
				i--;
//                if(this.childs[i].number + 1 <= bTreeOrder) {
//                    this.keys[this.number - 1] = key;
//                }
			}

// System.out.println("non-leaf node lookup key: " + this.keys[i]);

			return this.childs[i].insert(value, key);
		}

		@Override
		LeafNode<T, V> refreshLeft() {
			return this.childs[0].refreshLeft();
		}

		/**
		 * When the leaf node inserts successfully completes the decomposition,
		 * recursively inserts a new node to the parent node to maintain balance
		 * 
		 * @param node1
		 * @param node2
		 * @param key
		 */
		Node<T, V> insertNode(Node<T, V> node1, Node<T, V> node2, V key) {

// System.out.println("non-leaf node, insert key: " + node1.keys[node1.number - 1] + " " + node2.keys[node2.number - 1]);

			V oldKey = null;
			if (this.number > 0)
				oldKey = (V) this.keys[this.number - 1];
			// If the original key is null, indicating that this non-node is empty, you can
			// directly put in two nodes
			if (key == null || this.number <= 0) {
// System.out.println("non-leaf node, insert key: " + node1.keys[node1.number - 1] + " " + node2.keys[node2.number - 1] + "direct insert");
				this.keys[0] = node1.keys[node1.number - 1];
				this.keys[1] = node2.keys[node2.number - 1];
				this.childs[0] = node1;
				this.childs[1] = node2;
				this.number += 2;
				return this;
			}
			// The original node is not empty, you should first find the location of the
			// original node, and then insert the new node into the original node
			int i = 0;
			while (key.compareTo((V) this.keys[i]) != 0) {
				i++;
			}
			// The maximum value of the left node can be inserted directly, and the right
			// side should be moved and inserted.
			this.keys[i] = node1.keys[node1.number - 1];
			this.childs[i] = node1;

			Object tempKeys[] = new Object[maxNumber];
			Object tempChilds[] = new Node[maxNumber];

			System.arraycopy(this.keys, 0, tempKeys, 0, i + 1);
			System.arraycopy(this.childs, 0, tempChilds, 0, i + 1);
			System.arraycopy(this.keys, i + 1, tempKeys, 0, this.number - i - 1);
			System.arraycopy(this.childs, i + 1, tempChilds, 0, this.number - i - 1);
			tempKeys[i + 1] = node2.keys[node2.number - 1];
			tempChilds[i + 1] = node2;

			this.number++;

			// Determine whether you need to split
			// If you do not need to split, copy the array back, return directly
			if (this.number <= bTreeOrder) {
				System.arraycopy(tempKeys, 0, this.keys, 0, this.number);
				System.arraycopy(tempChilds, 0, this.childs, 0, this.number);

// System.out.println("non-leaf node, insert key: " + node1.keys[node1.number - 1] + " " + node2.keys[node2.number - 1] + ", no split required" );

				return null;
			}

// System.out.println("non-leaf node, insert key: " + node1.keys[node1.number - 1] + " " + node2.keys[node2.number - 1] + ", need to split") ;

			// If you need to split, and similar to the removal of the leaf node, open from
			// the middle
			Integer middle = this.number / 2;

			// Create a new non-leaf node, as the right half of the split
			BPlusNode<T, V> tempNode = new BPlusNode<T, V>();
			// After the non-leaf node split, the parent node pointer of its child node
			// should be updated to the correct pointer
			tempNode.number = this.number - middle;
			tempNode.parent = this.parent;
			// If the parent node is empty, create a new non-leaf node as the parent node,
			// and let the pointers of the two non-leaf nodes that are successfully split
			// point to the parent node.
			if (this.parent == null) {

// System.out.println("non-leaf node, insert key: " + node1.keys[node1.number - 1] + " " + node2.keys[node2.number - 1] + ", create new parent node") ;

				BPlusNode<T, V> tempBPlusNode = new BPlusNode<>();
				tempNode.parent = tempBPlusNode;
				this.parent = tempBPlusNode;
				oldKey = null;
			}
			System.arraycopy(tempKeys, middle, tempNode.keys, 0, tempNode.number);
			System.arraycopy(tempChilds, middle, tempNode.childs, 0, tempNode.number);
			for (int j = 0; j < tempNode.number; j++) {
				tempNode.childs[j].parent = tempNode;
			}
			// Let the original non-leaf node as the left node
			this.number = middle;
			this.keys = new Object[maxNumber];
			this.childs = new Node[maxNumber];
			System.arraycopy(tempKeys, 0, this.keys, 0, middle);
			System.arraycopy(tempChilds, 0, this.childs, 0, middle);

			// After the leaf node is successfully split, the newly generated node needs to
			// be inserted into the parent node.
			BPlusNode<T, V> parentNode = (BPlusNode<T, V>) this.parent;
			return parentNode.insertNode(this, tempNode, oldKey);
		}

	}

	
	
	
	
	
	/**
	 * Leaf node class
	 * 
	 * @param <T>
	 * @param <V>
	 */
	class LeafNode<T, V extends Comparable<V>> extends Node<T, V> {

		protected Object values[];
		protected LeafNode left;
		protected LeafNode right;

		public LeafNode() {
			super();
			this.values = new Object[maxNumber];
			this.left = null;
			this.right = null;
		}

		/**
		 * Find, classic binary search, no more comments
		 * 
		 * @param key
		 * @return
		 */
		@Override
		T find(V key) {
			if (this.number <= 0)
				return null;

// System.out.println("leaf node lookup");

			Integer left = 0;
			Integer right = this.number;

			Integer middle = (left + right) / 2;

			while (left < right) {
				V middleKey = (V) this.keys[middle];
				if (key.compareTo(middleKey) == 0)
					return (T) this.values[middle];
				else if (key.compareTo(middleKey) < 0)
					right = middle;
				else
					left = middle;
				middle = (left + right) / 2;
			}
			return null;
		}

		/**
		 *
		 * @param value
		 * @param key
		 */
		@Override
		Node<T, V> insert(T value, V key) {

// System.out.println("leaf node, insert key: " + key);

			// Save the key value of the original existence of the parent node
			V oldKey = null;
			if (this.number > 0)
				oldKey = (V) this.keys[this.number - 1];
			// Insert data first
			int i = 0;
			while (i < this.number) {
				if (key.compareTo((V) this.keys[i]) < 0)
					break;
				i++;
			}

			// Copy the array, complete the addition
			Object tempKeys[] = new Object[maxNumber];
			Object tempValues[] = new Object[maxNumber];
			System.arraycopy(this.keys, 0, tempKeys, 0, i);
			System.arraycopy(this.values, 0, tempValues, 0, i);
			System.arraycopy(this.keys, i, tempKeys, i + 1, this.number - i);
			System.arraycopy(this.values, i, tempValues, i + 1, this.number - i);
			tempKeys[i] = key;
			tempValues[i] = value;

			this.number++;

// System.out.println("Insert completed, current node key is:");
//            for(int j = 0; j < this.number; j++)
//                System.out.print(tempKeys[j] + " ");
//            System.out.println();

			// Determine whether you need to split
			// If you do not need to split to complete the copy and return directly
			if (this.number <= bTreeOrder) {
				System.arraycopy(tempKeys, 0, this.keys, 0, this.number);
				System.arraycopy(tempValues, 0, this.values, 0, this.number);

				// It is possible that although there is no node split, the value actually
				// inserted is greater than the original maximum, so the boundary values ​​of
				// all parent nodes are updated.
				Node node = this;
				while (node.parent != null) {
					V tempkey = (V) node.keys[node.number - 1];
					if (tempkey.compareTo((V) node.parent.keys[node.parent.number - 1]) > 0) {
						node.parent.keys[node.parent.number - 1] = tempkey;
						node = node.parent;
					}
				}
// System.out.println("leaf node, insert key: " + key + ", no splitting is required);

				return null;
			}

// System.out.println("leaf node, insert key: " + key + ", need to split");

			// If you need to split, split the node from the middle of the two parts
			Integer middle = this.number / 2;

			// New leaf node, as the right half of the split
			LeafNode<T, V> tempNode = new LeafNode<T, V>();
			tempNode.number = this.number - middle;
			tempNode.parent = this.parent;
			// If the parent node is empty, create a new non-leaf node as the parent node,
			// and let the pointers of the two leaf nodes that are successfully split point
			// to the parent node.
			if (this.parent == null) {

// System.out.println("leaf node, insert key: " + key + ", parent node is empty, create new parent node");

				BPlusNode<T, V> tempBPlusNode = new BPlusNode<>();
				tempNode.parent = tempBPlusNode;
				this.parent = tempBPlusNode;
				oldKey = null;
			}
			System.arraycopy(tempKeys, middle, tempNode.keys, 0, tempNode.number);
			System.arraycopy(tempValues, middle, tempNode.values, 0, tempNode.number);

			// Let the original leaf node as the left half of the split
			this.number = middle;
			this.keys = new Object[maxNumber];
			this.values = new Object[maxNumber];
			System.arraycopy(tempKeys, 0, this.keys, 0, middle);
			System.arraycopy(tempValues, 0, this.values, 0, middle);

			this.right = tempNode;
			tempNode.left = this;

			// After the leaf node is successfully split, the newly generated node needs to
			// be inserted into the parent node.
			BPlusNode<T, V> parentNode = (BPlusNode<T, V>) this.parent;
			return parentNode.insertNode(this, tempNode, oldKey);
		}

		@Override
		LeafNode<T, V> refreshLeft() {
			if (this.number <= 0)
				return null;
			return this;
		}
	}
}

package tree;

public class BinaryTreeInOrderTraversal {
	Node root;

	public static void main(String[] args) {
		BinaryTreeInOrderTraversal tree = new BinaryTreeInOrderTraversal();
		tree.add(50);
		tree.add(25);
		tree.add(75);
		tree.add(10);
		tree.add(30);
		tree.add(60);
		tree.add(65);
		tree.add(80);
		tree.add(85);
		tree.add(1);
		tree.add(100);
		
		System.out.println(" Display in order:  ");
		tree.traverse();

	}
	
	public void add(int  data){
		 Node nodeToAdd = new Node(data);
		 if(root == null){
			 root = nodeToAdd;
		 }
		 
		 //if data < node traverse left child, else traverse right child
		 // until we get to a node we can't traverse
		 
		 Node traversingNode = root;
		 traverseAndAdd(traversingNode,nodeToAdd );
	}
	
	private void traverseAndAdd(Node traversingNode,  Node nodeToAdd){
		if(nodeToAdd.data < traversingNode.data){
			if(traversingNode.leftChild == null){
				traversingNode.leftChild = nodeToAdd;
			}
			else{
				traverseAndAdd(traversingNode.leftChild,  nodeToAdd);
			}
		}
		else if(nodeToAdd.data > traversingNode.data){
			if(traversingNode.rightChild == null){
				traversingNode.rightChild = nodeToAdd;
			}
			else{
				traverseAndAdd(traversingNode.rightChild,  nodeToAdd);
			}
		}
	}
	
	public void traverse(){
		if (root != null) {
			Node nodeToTraverse = root;
			if (nodeToTraverse.leftChild == null && nodeToTraverse.rightChild == null) {
				System.out.println(nodeToTraverse.data);
			} else {
				inOrderTraversal(nodeToTraverse);
			}

		}
	}
	
	private void inOrderTraversal(Node node){
		if(node.leftChild != null){
			inOrderTraversal(node.leftChild);
		}
		
		System.out.println(node.data);
		
		if(node.rightChild != null){
			inOrderTraversal(node.rightChild);
		}
	}

}

package application;



public class TreeNode {
	public TreeNode[] children;
	public char letter;
	public boolean canEndWord;
	
	public TreeNode(char l) {
		this.letter = l;
		this.children = new TreeNode[26];
		this.canEndWord = false;
	}
}

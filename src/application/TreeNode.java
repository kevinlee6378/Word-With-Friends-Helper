package application;



public class TreeNode {
	private TreeNode[] children;
	private char letter;
	private boolean canEndWord;
	
	public TreeNode(char l) {
		this.letter = l;
		this.children = new TreeNode[26];
		this.canEndWord = false;
	}
	
	public TreeNode[] getChildren() {
		return this.children;
	}
	
	public char getLetter() {
		return this.letter;
	}
	
	public boolean canEndWord() {
		return this.canEndWord;
	}
	
	public void setCanEndWord(boolean canEndWord) {
		this.canEndWord = canEndWord;
	}
}

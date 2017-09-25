package application;

import java.util.LinkedList;

public class TreeNode {
	public LinkedList<TreeNode> children;
	public char letter;
	public boolean canEndWord;
	
	public TreeNode(char l) {
		this.letter = l;
		this.children = new LinkedList<>();
		this.canEndWord = false;
	}
}

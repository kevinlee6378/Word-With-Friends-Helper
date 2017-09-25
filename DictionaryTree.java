package application;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;;

public class DictionaryTree {
	public TreeNode baseNode;
	public DictionaryTree() {
		this.baseNode = new TreeNode('&');
	}
	
	public void AddWord(String word) {
		char[] charArray = word.toCharArray();
		TreeNode currentNode = this.baseNode;
		int i = 0;
		while (i < charArray.length) {
			char currentLetter = charArray[i];
			boolean nodeHasLetterAsChild = false;
			TreeNode childNode = null;
			for (TreeNode node : currentNode.children) {
				if (node.letter == currentLetter) {
					nodeHasLetterAsChild = true;
					childNode = node;
					break;
				}
			}
			if (nodeHasLetterAsChild){
				currentNode = childNode;
			}
			else {
				childNode = new TreeNode(currentLetter);
				currentNode.children.add(childNode);
				currentNode = childNode;
			}
			if (i == charArray.length - 1) {
				currentNode.canEndWord = true;
			}
			i++;
		}
	}
	
	public boolean isValidWord(String word) {
		char[] charArray = word.toCharArray();
		boolean isValid = true;
		TreeNode currentNode = this.baseNode;
		int i = 0;
		while (i < charArray.length) {
			char currentLetter = charArray[i];
			boolean nodeHasLetterAsChild = false;
			TreeNode childNode = null;
			for (TreeNode node : currentNode.children) {
				if (node.letter == currentLetter) {
					nodeHasLetterAsChild = true;
					childNode = node;
					break;
				}
			}
			if (nodeHasLetterAsChild) {
				currentNode = childNode;
			}
			else {
				isValid = false;
				break;
			}
			if (i == charArray.length - 1) {
				if (!currentNode.canEndWord) {
					isValid = false;
				}
			}
			i++;
		}
		return isValid;
	}
}

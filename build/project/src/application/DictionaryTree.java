package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class DictionaryTree {

	private TreeNode baseNode;
	private static BufferedReader b;
	
	public DictionaryTree(String file) {
		this.baseNode = new TreeNode('&');
		try {
			File f = new File(file);
			b = new BufferedReader(new FileReader(f));
			String readLine = "";

			while ((readLine = b.readLine()) != null) {
				this.addWord(readLine);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addWord(String word) {
		char[] charArray = word.toCharArray();
		TreeNode currentNode = this.baseNode;
		int i = 0;
		while (i < charArray.length) {
			char currentLetter = charArray[i];
			int position = getPosition(currentLetter);
			if (currentNode.children[position] != null){
				currentNode = currentNode.children[position];
			}
			else {
				currentNode.children[position] =  new TreeNode(currentLetter);
				currentNode = currentNode.children[position];
			}
			if (i == charArray.length - 1) {
				currentNode.canEndWord = true;
			}
			i++;
		}
	}
	public boolean[] validAndFutureLetters(String word, char[] letters) {
		char[] charArray = word.toCharArray();
		TreeNode currentNode = this.baseNode;
		boolean[] returnArray = new boolean[letters.length + 1];
		int i = 0;
		while (i < charArray.length) {
			char currentLetter = charArray[i];
			int position = getPosition(currentLetter);
			TreeNode childNode = currentNode.children[position];
			if (childNode != null) {
				currentNode = childNode;
			}
			else {
				//isValid = false;
				break;
			}
			if (i == charArray.length - 1) {
				int[] positions = new int[letters.length];
				for (int j = 0; j < letters.length; j++) {
					positions[j] = letters[j] - 'a';
				}
				for (int j = 0; j < letters.length; j++) {
					returnArray[j] = currentNode.children[positions[j]] == null ? false : true;
				}
				if(currentNode.canEndWord) {
					returnArray[returnArray.length - 1] = true; 
				}
			}
			i++;
		}
		return returnArray;
	}
	public boolean isValidWord(String word) {
		char[] charArray = word.toCharArray();
		TreeNode currentNode = this.baseNode;
		boolean isValid = true;
		int i = 0;
		while (i < charArray.length) {
			char currentLetter = charArray[i];
			int position = getPosition(currentLetter);
			TreeNode childNode = currentNode.children[position];
			if (childNode != null) {
				currentNode = childNode;
			}
			else {
				isValid = false;
				break;
			}
			if (i == charArray.length - 1) {
				if(!currentNode.canEndWord) {
					isValid = false;
				}
			}
			i++;
		}
		return isValid;
	}
	public int getPosition(char c) {
		return c - 'a';
	}
}
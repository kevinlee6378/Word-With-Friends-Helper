package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class DictionaryTree {
	private static BufferedReader b;

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
			int position = currentLetter - 'a';
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
		//boolean isValid = true;
		boolean[] returnArray = new boolean[letters.length + 1];
		int i = 0;
		while (i < charArray.length) {
			char currentLetter = charArray[i];
			int position = currentLetter - 'a';
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
			int position = currentLetter - 'a';
			//int position = getPosition(currentLetter);
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
	public static void main(String[] args) {
//		DictionaryTree dt = new DictionaryTree();
//		try {
//			File f = new File("src/application/wordlist.txt");
//	        b = new BufferedReader(new FileReader(f));
//	        String readLine = "";
//
//			while ((readLine = b.readLine()) != null) {
//	            //System.out.println(readLine);
//	            dt.AddWord(readLine);
//	        }
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		char[] letters = {'a', 'e', 'i', 'o', 'u'}; 
//		System.out.println(Arrays.toString(dt.validAndFutureLetters("hell", letters )));
//		System.out.println(dt.isValidWord("hello"));
//		System.out.println(dt.isValidWord("hell"));
//		System.out.println(dt.isValidWord("absence"));

	}
}
/*
 *  Copyright (C) 2013, Universidad Simon Bolivar
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package ve.usb.gdbb;

/*
 * Class used for remember nodes in hypergraphdb
 * Credits for http://exceptional-code.blogspot.com/2011/07/coding-up-trie-prefix-tree.html
 */
public class Trie{
	public static int offset = 30;
	public static TrieNode createTree(){
		return(new TrieNode('\0'));
	}

	/*
	 * Method for insert word in trie
	 */
	public static void insertWord(TrieNode root, String word){

		int l = word.length();
		char[] letters = word.toCharArray();
		TrieNode curNode = root;

		for (int i = 0; i < l; i++)
		{
			if (curNode.links[letters[i]-offset] == null)
				curNode.links[letters[i]-offset] = new TrieNode(letters[i]);
			curNode = curNode.links[letters[i]-offset];
		}
		curNode.fullWord = true; 
	}

	/*
	 * Method for find if a word exist in the trie
	 */
	public static boolean find(TrieNode root, String word){
		char[] letters = word.toCharArray();
		int l = letters.length;
		TrieNode curNode = root;

		int i;
		for (i = 0; i < l; i++)
		{
			if (curNode == null)
				return false;
			curNode = curNode.links[letters[i]-offset];
		}

		if (i == l && curNode == null)
			return false;

		if (curNode != null && !curNode.fullWord)
			return false;

		return true;
	}
}


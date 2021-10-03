package org.myutilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Utilities {
	public static int getRandom(int max) {
		return (int) (Math.random() * max);
	}
	
	public static boolean isCollectionSorted(ArrayList<String> list) {
	    List<String> copy = new ArrayList<String>(list);
	    Collections.sort(copy);
	    return copy.equals(list);
	}
}

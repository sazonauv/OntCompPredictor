package uk.ac.manchester.cs.sazonau.ontology.features;

import java.util.Comparator;

/**
 * @author Viachaslau Sazonau
 * COMP60342: Lab Exercise 1
 * 
 * The class is used as a comparator to receive indexes of array elements
 * after sorting.
 * It does not change an input array but only produce indexes.
 */
public class AIComparator implements Comparator<Integer> {
	// sort ascending
	public static final String asc = "asc";
	// sort descending
	public static final String desc = "desc";

	// An array for sorting in the object form
	private Double[] array;

	private String order;

	/**
	 * @param array: An array for sorting
	 */
	public AIComparator(int[] array, String order) {
		this.order = order;
		toObjectArray(array);
	}
	
	/**
	 * @param array: An array for sorting
	 */
	public AIComparator(double[] array, String order) {
		this.order = order;
		toObjectArray(array);
	}	
		
	/**Translates an input array to an object array
	 * 
	 * @param array: An array for sorting
	 */
	private void toObjectArray(int[] array) {
		this.array = new Double[array.length];
		for (int i=0; i<array.length; i++) {
			this.array[i] = new Double(array[i]);
		}
	}
	
	/**Translates an input array to an object array
	 * 
	 * @param array: An array for sorting
	 */
	private void toObjectArray(double[] array) {
		this.array = new Double[array.length];
		for (int i=0; i<array.length; i++) {
			this.array[i] = new Double(array[i]);
		}
	}
	
	/**
	 * @return indexes of array elements after sorting
	 */
	public Integer[] createIndexArray() {
		Integer[] indexes = new Integer[array.length];
		for (int i=0; i<array.length; i++) {
			indexes[i] = i;
		}
		return indexes;
	}

	@Override
	public int compare(Integer index1, Integer index2) {
		if (order.equals(asc)) {
			return array[index1].compareTo(array[index2]);
		} else {
			return - array[index1].compareTo(array[index2]);
		}
	}
}

package gov.usgs.volcanoes.core.math;

/**
 * Vector of integer values.
 * Supports auto-resizing during elements adding.
 * 
 * @author Dan Cervelli
 */
public class IntVector {
  private int[] ints;
  private int growBy;
  private int size;

  /**
   * Constructor
   * @param is initial vector size
   * @param gb growth parameter, increment size during vector autoincreasing 
   */
  public IntVector(int is, int gb) {
    ints = new int[is];
    growBy = gb;
  }

  /**
   * Adds integer value to vector
   * @param i value to add
   */
  public void add(int i) {
    if (size == ints.length) {
      int[] newInts = new int[ints.length + growBy];
      System.arraycopy(ints, 0, newInts, 0, size);
      ints = newInts;
    }
    ints[size++] = i;
  }

  /**
   * Getter for size
   * @return vector size, i.e count of added elements
   */
  public int size() {
    return size;
  }

  /**
   * Getter for ith element
   * @param i
   * @return i-th element of vector
   */
  public int elementAt(int i) {
    return ints[i];
  }

  /**
   * Getter for ints as an array
   * @return vector content as integer array, including allocated but not used space
   */
  public int[] getInts() {
    return ints;
  }

  /**
   * Getter for ints as an array
   * @return vector content as integer array, only used space
   */
  public int[] getResizedInts() {
    int[] newInts = new int[size];
    System.arraycopy(ints, 0, newInts, 0, size);
    return newInts;
  }
}

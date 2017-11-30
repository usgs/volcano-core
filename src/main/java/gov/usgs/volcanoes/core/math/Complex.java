package gov.usgs.volcanoes.core.math;

/**
 * A class for complex numbers. All methods of this class return a new complex
 * number with the result of the operation without altering the original value
 * of the complex. The only way to change the actual values of the complex
 * number are through the public variables re and im.
 *
 * <p>Most of this code was translated from the files complex.C and complex.h by:<br>
 * <br>
 * A.J. Fisher, University of York, fisher@minster.york.ac.uk<br>
 * September 1992<br>
 * as part of his software for generating digital IIR filters.
 *
 * @author Dan Cervelli
 */
public class Complex {
  /**
   * Evaluates a complex polynomial.
   * 
   * @param coeffs the coefficents
   * @param n the number of coefficients
   * @param z the complex
   * @return the result
   */
  public static Complex eval(Complex[] coeffs, int n, Complex z) {
    Complex sum = new Complex();
    for (int i = n; i >= 0; i--) {
      sum = (sum.mult(z)).plus(coeffs[i]);
    }
    return sum;
  }

  /**
   * Does eval(top) / eval(bottom).
   * 
   * @param topco the top coefficents
   * @param nt the number of top coefficients
   * @param botco the bottom coefficents
   * @param nb the number of bottom coefficients
   * @param z the complex
   * @return the result
   */
  public static Complex evaluate(Complex[] topco, int nt, Complex[] botco, int nb, Complex z) {
    final Complex c1 = eval(topco, nt, z);
    final Complex c2 = eval(botco, nb, z);
    return c1.divide(c2);
  }

  /**
   * Gets a new complex of value [cos(theta), sin(theta)].
   * 
   * @param theta the angle
   * @return the result
   */
  public static Complex expj(double theta) {
    return new Complex(Math.cos(theta), Math.sin(theta));
  }

  public static Complex expk(double theta, int k) {
    return new Complex(Math.cos(k * theta), Math.sin(k * theta));
  }

  /**
   * The imaginary part of this complex number.
   */
  public double im;

  /**
   * The real part of this complex number.
   */
  public double re;

  /**
   * Constructs a 0,0 complex number.
   */
  public Complex() {
    re = 0;
    im = 0;
  }

  /**
   * Contructs a complex number from another.
   * 
   * @param c the other complex number
   */
  public Complex(Complex c) {
    re = c.re;
    im = c.im;
  }

  /**
   * Contructs a complex number from arguments.
   * 
   * @param r the real part
   * @param i the imaginary part
   */
  public Complex(double r, double i) {
    re = r;
    im = i;
  }

  /**
   * Gets the arctangent of this complex.
   * 
   * @return the arctangent
   */
  public double atan2() {
    return Math.atan2(im, re);
  }

  /**
   * Performs the bilinear transformation.
   * 
   * @return the result
   */
  public Complex blt() {
    final Complex c1 = new Complex(2 + re, im);
    final Complex c2 = new Complex(2 - re, -im);
    return c1.divide(c2);
  }

  /**
   * Returns the conjugate of this complex.
   * 
   * @return the conjugate
   */
  public Complex conj() {
    return new Complex(re, -im);
  }

  /**
   * Divides this complex by another complex.
   * 
   * @param c the other
   * @return the result
   */
  public Complex divide(Complex c) {
    final double mag = c.re * c.re + c.im * c.im;
    return new Complex((re * c.re + im * c.im) / mag, (im * c.re - re * c.im) / mag);
  }

  /**
   * Divides this complex by a scalar.
   * 
   * @param d the scalar
   * @return the result
   */
  public Complex divide(double d) {
    return new Complex(re / d, im / d);
  }

  /**
   * Tests for equality.
   * 
   * @param other the other complex
   * @return whether or not these complexes are equal
   */
  @Override
  public boolean equals(Object other) {
    if (!(other instanceof Complex)) {
      return false;
    } else {
      Complex c = (Complex) other;
      return (c.re == re && c.im == im);
    }
  }

  /**
   * Gets the exponential of this complex.
   * 
   * @return the exponential
   */
  public Complex exp() {
    return expj(im).mult(Math.exp(re));
  }

  /**
   * Gets the hypotenuse of this complex. Synonym for mag()
   * 
   * @return the hypotenuse
   */
  public double hypot() {
    return Math.sqrt(im * im + re * re);
  }

  /**
   * Gets the magnitude of this complex number.
   * 
   * @return the magnitude
   */
  public double mag() {
    return Math.sqrt(re * re + im * im);
  }

  /**
   * Substracts another complex from this complex.
   * 
   * @param c the other
   * @return the result
   */
  public Complex minus(Complex c) {
    return new Complex(re - c.re, im - c.im);
  }

  /**
   * Multiplies this complex by another.
   * 
   * @param c the other complex
   * @return the result
   */
  public Complex mult(Complex c) {
    return new Complex(re * c.re - im * c.im, re * c.im + im * c.re);
  }

  /**
   * Multiplies this complex by a scalar.
   * 
   * @param d the scalar
   * @return the result
   */
  public Complex mult(double d) {
    return new Complex(re * d, im * d);
  }

  /**
   * Negates this complex.
   * 
   * @return the negation
   */
  public Complex neg() {
    return new Complex(-re, -im);
  }

  /**
   * Adds this complex to another complex.
   * 
   * @param c the other
   * @return the result
   */
  public Complex plus(Complex c) {
    return new Complex(re + c.re, im + c.im);
  }

  /**
   * Perform exponentiation.
   * 
   * @param k exponent
   * @return result
   */
  public Complex pow(int k) {
    final double rk = Math.pow(mag(), k);
    final double t = atan2();
    return new Complex(rk * Math.cos(k * t), rk * Math.sin(k * t));
  }

  /**
   * Gets the square of this complex.
   * 
   * @return the result
   */
  public Complex sqr() {
    return this.mult(this);
  }

  /**
   * Gets the complex square root.
   * 
   * @return the square root
   */
  public Complex sqrt() {
    final double r = hypot();
    final Complex z = new Complex(Math.sqrt(0.5 * (r + re)), Math.sqrt(0.5 * (r - re)));
    if (im < 0) {
      z.im = -z.im;
    }
    return z;
  }

  /**
   * Gets a string representation of this complex.
   * 
   * @return the string representation
   */
  @Override
  public String toString() {
    return "re=" + re + ", im=" + im;
  }

}

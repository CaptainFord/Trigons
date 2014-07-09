/**
 * 
 */
package vordeka.util.exception;

/**
 * An exception that cannot occur.
 * Declared only because I would want to know
 * if something impossible occurred.
 * <p>
 * &lt;/humor&gt;I want to point out that when I use this class, I don't <em>really</em> 
 * think that the exception is <em>impossible</em>, otherwise I wouldn't
 * even bother with the exception. I usually use this class to wrap other
 * exceptions, such as when overriding clone(). As long as the class implements
 * Cloneable, it shouldn't ever throw a CloneNotSupportedException ... but if it
 * does, I want to know about it.
 * </p>
 * Basically, if I ever see an ImpossibleException pop up, that means that
 * I never expected the exception to come up. That it did obviously indicates
 * that I misunderstood the code -- this in itself can be very helpful for 
 * tracking down the cause, because it helps me remember what I was thinking
 * about when I wrote the code (I pretty much remember every time I use this
 * exception, aside from when I'm just wrapping other exceptions).
 * <p>
 * I think I've put a lot more thought into this than I really need to.
 * @author Alex
 *
 */
public class ImpossibleException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2135041030648828209L;

	public ImpossibleException() {
		super();
	}

	public ImpossibleException(String message, Throwable cause) {
		super(message, cause);
	}

	public ImpossibleException(String message) {
		super(message);
	}

	public ImpossibleException(Throwable cause) {
		super(cause);
	}

	
}

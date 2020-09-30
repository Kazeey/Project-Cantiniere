// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.service.exception;

/**
 * Exception when a parameter is invalid.
 */
public class ParameterException extends IllegalArgumentException {
	private static final long serialVersionUID = 1L;
	/**
	 * Constructs a new exception with {@code null} as its detail message. The cause
	 * is not initialized, and may subsequently be initialized by a call to
	 * {@link #initCause}.
	 */
	private String parameterName;

	/**
	 * Constructor of the object.
	 */
	public ParameterException() {
		super();
	}

	/**
	 * Constructs a new runtime exception with the specified detail message. The
	 * cause is not initialized, and may subsequently be initialized by a call to
	 * {@link #initCause}.
	 *
	 * @param pMessage the detail message. The detail message is saved for later
	 *                 retrieval by the {@link #getMessage()} method.
	 */
	public ParameterException(String pMessage) {
		super(pMessage);
	}

	/**
	 * Constructs a new runtime exception with the specified detail message. The
	 * cause is not initialized, and may subsequently be initialized by a call to
	 * {@link #initCause}.
	 *
	 * @param pMessage       the detail message. The detail message is saved for
	 *                       later retrieval by the {@link #getMessage()} method.
	 * @param pParameterName the name of the parameter
	 */
	public ParameterException(String pMessage, String pParameterName) {
		super(pMessage);
		this.parameterName = pParameterName;
	}

	/**
	 * Constructs a new exception with the specified cause and a detail message of
	 * <tt>(cause==null ? null : cause.toString())</tt> (which typically contains
	 * the class and detail message of <tt>cause</tt>). This constructor is useful
	 * for exceptions that are little more than wrappers for other throwables (for
	 * example, {@link java.security.PrivilegedActionException}).
	 *
	 * @param pCause the cause (which is saved for later retrieval by the
	 *               {@link Throwable#getCause()} method). (A <tt>null</tt> value is
	 *               permitted, and indicates that the cause is nonexistent or
	 *               unknown.)
	 * @since 1.5
	 */
	public ParameterException(Throwable pCause) {
		super(pCause);
	}

	/**
	 * Constructs a new exception with the specified detail message and cause.
	 *
	 * <p>
	 * Note that the detail message associated with <code>cause</code> is <i>not</i>
	 * automatically incorporated in this exception's detail message.
	 *
	 * @param pMessage the detail message (which is saved for later retrieval by the
	 *                 {@link Throwable#getMessage()} method).
	 * @param pCause   the cause (which is saved for later retrieval by the
	 *                 {@link Throwable#getCause()} method). (A <tt>null</tt> value
	 *                 is permitted, and indicates that the cause is nonexistent or
	 *                 unknown.)
	 * @since 1.5
	 */
	public ParameterException(String pMessage, Throwable pCause) {
		super(pMessage, pCause);
	}

	/**
	 * Gets the attribute value.
	 *
	 * @return the parameterName value.
	 */
	public String getParameterName() {
		return this.parameterName;
	}

	/**
	 * Sets the attribute value.
	 *
	 * @param pParamterName the new value for parameterName attribute
	 */
	public void setParameterName(String pParamterName) {
		this.parameterName = pParamterName;
	}

}

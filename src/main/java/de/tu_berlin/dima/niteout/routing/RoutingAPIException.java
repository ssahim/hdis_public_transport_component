package de.tu_berlin.dima.niteout.routing;

import static de.tu_berlin.dima.niteout.routing.RoutingAPIException.ErrorCode.*;

/**
 * The class {@code RoutingAPIException} is a form of {@code Exception} that
 * indicates conditions that an application using the RoutingAPI might want to
 * catch.
 * <p>
 * The {@code RoutingAPIException} and its subclasses also provides information
 * why the process went wrong and is the only data class used by the
 * {@code RoutingAPI} to express failing conditions.
 *
 * @author Thomas Wirth
 */
public class RoutingAPIException extends Exception {

    private final ErrorCode code;

    /**
     * Constructs a new exception with {@code null} as its detail message and
     * an error code to describe the failing condition. The cause is not
     * initialized, and may subsequently be initialized by a call to
     * {@link #initCause}.
     *
     * @param code the error code describing the occurring condition
     */
    public RoutingAPIException(ErrorCode code) {
        super();
        this.code = code;
    }

    /**
     * Constructs a new exception with the error code and the specified
     * detail message. The cause is not initialized, and may subsequently
     * be initialized by a call to {@link #initCause}.
     *
     * @param code    the error code describing the occurring condition
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public RoutingAPIException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * Constructs a new exception with the error code, specified cause and
     * a detail message.
     *
     * @param code  the error code describing the occurring condition
     * @param cause the cause exception
     */
    public RoutingAPIException(ErrorCode code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    /**
     * Constructs a new exception of the routing api with the error code, the
     * specified detail message and cause.
     * <p>
     * Note that the detail message associated with {@code cause} is
     * <i>not</i> automatically incorporated in this exception's detail message.
     *
     * @param code    the error code describing the occurring condition
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link #getCause()} method).  (A <tt>null</tt> value is
     *                permitted, and indicates that the cause is nonexistent or
     *                unknown.)
     */
    public RoutingAPIException(ErrorCode code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    /**
     * Returns the error code of this RoutingAPIException which describes the
     * condition that occurred and finally lead to the error.
     *
     * @return  the error code describing the failure condition, which may be

     */
    public ErrorCode getCode() {
        return code;
    }

    public static RoutingAPIException buildFromStatusCode(int statusCode, String message) {
        return new RoutingAPIException(getErrorForStatusCode(statusCode), message);
    }

    public static RoutingAPIException buildFromStatusCode(int statusCode) {
        return new RoutingAPIException(getErrorForStatusCode(statusCode));
    }

    private static ErrorCode getErrorForStatusCode(int statusCode) {
        switch (statusCode) {
            case 200:
                throw new IllegalArgumentException("200 is not an erroneous status code!");
            case 400:
                return API_ERROR_BAD_REQUEST;
            case 404:
                return API_ERROR_NOT_FOUND;
            case 405:
                return API_ERROR_WRONG_METHOD;
            case 500:
                return API_ERROR_INTERNAL;
            case 501:
                return API_ERROR_NOT_IMPLEMENTED;
            default:
                return HTTP;
        }
    }



    public enum ErrorCode {

        API_CREDENTIALS_INVALID("API credentials to request the data source were missing or invalid"),
        DATA_SOURCE_RESPONSE_INVALID("The RoutingAPI could not handle the response from the server"),
        INVALID_TRANSPORT_MODE("An invalid transport mode was requested to the RoutingAPI"),
        INVALID_URI_SYNTAX("Could not build a valid URI"),
        HTTP("Something went wrong when requesting the api, see chained exception"),
        API_ERROR_BAD_REQUEST("Request to data source was corrupted, see exception message, if available"),
        API_ERROR_WRONG_METHOD("405 - The used HTTP method was wrong, maybe try GET or POST"),
        API_ERROR_NOT_FOUND("404 - Request endpoint was not found"),
        API_ERROR_INTERNAL("500 - Data source API had an internal error"),
        API_ERROR_NOT_IMPLEMENTED("501 - Request endpoint not implemented"),
        PROCESS_RESPONSE_ERROR_JSON("Error while processing JSON response");

        public final String message;

        ErrorCode(String message) {
            this.message = message;
        }

    }
}

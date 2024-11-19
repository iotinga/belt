package io.tinga.belt.output;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum representing status codes.
 */
public enum Status {
    // Request 0
    /**
     * 0 Request: Identifies a client Request message.
     */
    REQUEST(0, Category.REQUEST),

    // Informational 1xx
    /**
     * 100 Continue: The client should continue with its request.
     */
    CONTINUE(100, Category.INFORMATIONAL),
    /**
     * 101 Switching Protocols: The server agrees to switch protocols.
     */
    SWITCHING_PROTOCOLS(101, Category.INFORMATIONAL),
    /**
     * 102 Processing: The server has received the request but has not yet completed it.
     */
    PROCESSING(102, Category.INFORMATIONAL),

    // Success 2xx
    /**
     * 200 OK: The request has succeeded.
     */
    OK(200, Category.SUCCESS),
    /**
     * 201 Created: The request has been fulfilled and has resulted in one or more new resources being created.
     */
    CREATED(201, Category.SUCCESS),
    /**
     * 202 Accepted: The request has been accepted for processing, but the processing has not been completed.
     */
    ACCEPTED(202, Category.SUCCESS),
    /**
     * 203 Non-Authoritative Information: The request has been successfully processed, but is returning information that may be from another source.
     */
    NON_AUTHORITATIVE_INFORMATION(203, Category.SUCCESS),
    /**
     * 204 No Content: The server successfully processed the request, but is not returning any content.
     */
    NO_CONTENT(204, Category.SUCCESS),
    /**
     * 205 Reset Content: The server successfully processed the request, but is not returning any content and requires that the requester reset the document view.
     */
    RESET_CONTENT(205, Category.SUCCESS),
    /**
     * 206 Partial Content: The server is delivering only part of the resource due to a range header sent by the client.
     */
    PARTIAL_CONTENT(206, Category.SUCCESS),

    // Redirection 3xx
    /**
     * 300 Multiple Choices: The request has more than one possible response.
     */
    MULTIPLE_CHOICES(300, Category.REDIRECTION),
    /**
     * 301 Moved Permanently: This and all future requests should be directed to the given URI.
     */
    MOVED_PERMANENTLY(301, Category.REDIRECTION),
    /**
     * 302 Found: The resource was found but temporarily located elsewhere.
     */
    FOUND(302, Category.REDIRECTION),
    /**
     * 303 See Other: The response can be found under a different URI and should be retrieved using a GET method.
     */
    SEE_OTHER(303, Category.REDIRECTION),
    /**
     * 304 Not Modified: The resource has not been modified since the last request.
     */
    NOT_MODIFIED(304, Category.REDIRECTION),
    /**
     * 305 Use Proxy: The requested resource is available only through a proxy, the address for which is provided in the response.
     */
    USE_PROXY(305, Category.REDIRECTION),
    /**
     * 307 Temporary Redirect: The request should be repeated with another URI; however, future requests should still use the original URI.
     */
    TEMPORARY_REDIRECT(307, Category.REDIRECTION),
    /**
     * 308 Permanent Redirect: The request and all future requests should be repeated using another URI.
     */
    PERMANENT_REDIRECT(308, Category.REDIRECTION),

    // Client Error 4xx
    /**
     * 400 Bad Request: The server cannot or will not process the request due to a client error.
     */
    BAD_REQUEST(400, Category.CLIENT_ERROR),
    /**
     * 401 Unauthorized: Authentication is required and has failed or has not yet been provided.
     */
    UNAUTHORIZED(401, Category.CLIENT_ERROR),
    /**
     * 402 Payment Required: Reserved for future use.
     */
    PAYMENT_REQUIRED(402, Category.CLIENT_ERROR),
    /**
     * 403 Forbidden: The server understood the request, but refuses to authorize it.
     */
    FORBIDDEN(403, Category.CLIENT_ERROR),
    /**
     * 404 Not Found: The requested resource could not be found.
     */
    NOT_FOUND(404, Category.CLIENT_ERROR),
    /**
     * 405 Method Not Allowed: A request method is not supported for the requested resource.
     */
    METHOD_NOT_ALLOWED(405, Category.CLIENT_ERROR),
    /**
     * 406 Not Acceptable: The requested resource is capable of generating only content not acceptable according to the Accept headers sent in the request.
     */
    NOT_ACCEPTABLE(406, Category.CLIENT_ERROR),
    /**
     * 407 Proxy Authentication Required: The client must first authenticate itself with the proxy.
     */
    PROXY_AUTHENTICATION_REQUIRED(407, Category.CLIENT_ERROR),
    /**
     * 408 Request Timeout: The server timed out waiting for the request.
     */
    REQUEST_TIMEOUT(408, Category.CLIENT_ERROR),
    /**
     * 409 Conflict: The request could not be completed due to a conflict with the current state of the resource.
     */
    CONFLICT(409, Category.CLIENT_ERROR),
    /**
     * 410 Gone: The resource requested is no longer available and will not be available again.
     */
    GONE(410, Category.CLIENT_ERROR),
    /**
     * 411 Length Required: The request did not specify the length of its content, which is required by the requested resource.
     */
    LENGTH_REQUIRED(411, Category.CLIENT_ERROR),
    /**
     * 412 Precondition Failed: The server does not meet one of the preconditions that the requester put on the request.
     */
    PRECONDITION_FAILED(412, Category.CLIENT_ERROR),
    /**
     * 413 Payload Too Large: The request is larger than the server is willing or able to process.
     */
    PAYLOAD_TOO_LARGE(413, Category.CLIENT_ERROR),
    /**
     * 414 URI Too Long: The URI provided was too long for the server to process.
     */
    URI_TOO_LONG(414, Category.CLIENT_ERROR),
    /**
     * 415 Unsupported Media Type: The request entity has a media type which the server or resource does not support.
     */
    UNSUPPORTED_MEDIA_TYPE(415, Category.CLIENT_ERROR),
    /**
     * 416 Range Not Satisfiable: The client has asked for a portion of the file, but the server cannot supply that portion.
     */
    RANGE_NOT_SATISFIABLE(416, Category.CLIENT_ERROR),
    /**
     * 417 Expectation Failed: The server cannot meet the requirements of the Expect request-header field.
     */
    EXPECTATION_FAILED(417, Category.CLIENT_ERROR),
    /**
     * 418 I'm a teapot: The server refuses to brew coffee because it is, permanently, a teapot.
     */
    IM_A_TEAPOT(418, Category.CLIENT_ERROR),
    /**
     * 421 Misdirected Request: The request was directed at a server that is not able to produce a response.
     */
    MISDIRECTED_REQUEST(421, Category.CLIENT_ERROR),
    /**
     * 422 Unprocessable Entity: The request was well-formed but was unable to be followed due to semantic errors.
     */
    UNPROCESSABLE_ENTITY(422, Category.CLIENT_ERROR),
    /**
     * 423 Locked: The resource that is being accessed is locked.
     */
    LOCKED(423, Category.CLIENT_ERROR),
    /**
     * 424 Failed Dependency: The request failed due to failure of a previous request.
     */
    FAILED_DEPENDENCY(424, Category.CLIENT_ERROR),
    /**
     * 426 Upgrade Required: The client should switch to a different protocol.
     */
    UPGRADE_REQUIRED(426, Category.CLIENT_ERROR),
    /**
     * 428 Precondition Required: The origin server requires the request to be conditional.
     */
    PRECONDITION_REQUIRED(428, Category.CLIENT_ERROR),
    /**
     * 429 Too Many Requests: The user has sent too many requests in a given amount of time.
     */
    TOO_MANY_REQUESTS(429, Category.CLIENT_ERROR),
    /**
     * 431 Request Header Fields Too Large: The server is unwilling to process the request because its header fields are too large.
     */
    REQUEST_HEADER_FIELDS_TOO_LARGE(431, Category.CLIENT_ERROR),
    /**
     * 451 Unavailable For Legal Reasons: The resource is unavailable for legal reasons.
     */
    UNAVAILABLE_FOR_LEGAL_REASONS(451, Category.CLIENT_ERROR),

    // Server Error 5xx
    /**
     * 500 Internal Server Error: The server encountered an unexpected condition that prevented it from fulfilling the request.
     */
    INTERNAL_SERVER_ERROR(500, Category.SERVER_ERROR),
    /**
     * 501 Not Implemented: The server does not support the functionality required to fulfill the request.
     */
    NOT_IMPLEMENTED(501, Category.SERVER_ERROR),
    /**
     * 502 Bad Gateway: The server, while acting as a gateway or proxy, received an invalid response from the upstream server.
     */
    BAD_GATEWAY(502, Category.SERVER_ERROR),
    /**
     * 503 Service Unavailable: The server is currently unable to handle the request due to temporary overloading or maintenance of the server.
     */
    SERVICE_UNAVAILABLE(503, Category.SERVER_ERROR),
    /**
     * 504 Gateway Timeout: The server, while acting as a gateway or proxy, did not receive a timely response from the upstream server.
     */
    GATEWAY_TIMEOUT(504, Category.SERVER_ERROR),
    /**
     * 505 Protocol Version Not Supported: The server does not support the protocol version used in the request.
     */
    PROTOCOL_VERSION_NOT_SUPPORTED(505, Category.SERVER_ERROR),
    /**
     * 506 Variant Also Negotiates: The server has an internal configuration error.
     */
    VARIANT_ALSO_NEGOTIATES(506, Category.SERVER_ERROR),
    /**
     * 507 Insufficient Storage: The server is unable to store the representation needed to complete the request.
     */
    INSUFFICIENT_STORAGE(507, Category.SERVER_ERROR),
    /**
     * 508 Loop Detected: The server detected an infinite loop while processing the request.
     */
    LOOP_DETECTED(508, Category.SERVER_ERROR),
    /**
     * 510 Not Extended: Further extensions to the request are required for the server to fulfill it.
     */
    NOT_EXTENDED(510, Category.SERVER_ERROR),
    /**
     * 511 Network Authentication Required: The client needs to authenticate to gain network access.
     */
    NETWORK_AUTHENTICATION_REQUIRED(511, Category.SERVER_ERROR);

    private final int code;
    private final Category category;

    Status(int code, Category category) {
        this.code = code;
        this.category = category;
    }

    @JsonValue
    public int getCode() {
        return code;
    }

    public Category getCategory() {
        return category;
    }

    @JsonCreator
    public static Status fromCode(int code) {
        for (Status status : Status.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown HTTP status code: " + code);
    }

    /**
     * Enum representing the category of HTTP status codes.
     */
    public enum Category {
        REQUEST, INFORMATIONAL, SUCCESS, REDIRECTION, CLIENT_ERROR, SERVER_ERROR
    }
}

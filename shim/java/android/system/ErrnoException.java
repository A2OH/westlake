package android.system;

import java.io.IOException;
import java.net.SocketException;

/**
 * Android-compatible ErrnoException shim.
 * Matches core-libart's android.system.ErrnoException signature exactly.
 * The core-libart version takes precedence at boot image time; this file
 * exists only so that shim code compiles against it.
 */
public final class ErrnoException extends Exception {

    private final String functionName;

    /** The errno value from the failed syscall. */
    public final int errno;

    public ErrnoException(String functionName, int errno) {
        this.functionName = functionName;
        this.errno = errno;
    }

    public ErrnoException(String functionName, int errno, Throwable cause) {
        super(cause);
        this.functionName = functionName;
        this.errno = errno;
    }

    @Override
    public String getMessage() {
        return functionName + " failed: errno=" + errno;
    }

    /**
     * Converts this ErrnoException to an IOException.
     */
    public IOException rethrowAsIOException() throws IOException {
        IOException ioe = new IOException(getMessage(), this);
        throw ioe;
    }

    /**
     * Converts this ErrnoException to a SocketException.
     */
    public SocketException rethrowAsSocketException() throws SocketException {
        throw new SocketException(getMessage());
    }
}

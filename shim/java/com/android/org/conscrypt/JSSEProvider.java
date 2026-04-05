package com.android.org.conscrypt;

import java.security.Provider;

/**
 * Stub Conscrypt JSSE provider.
 * Satisfies sun.security.jca.Providers initialization check.
 */
public final class JSSEProvider extends Provider {
    public JSSEProvider() {
        super("AndroidOpenSSL-JSSE", 1.0, "Westlake JSSE stub");
    }
}

package java.io;

public class DirectPrintStream extends PrintStream {
    private final int fd;
    
    public DirectPrintStream(int fd) {
        super((OutputStream) null);
        this.fd = fd;
    }
    
    private static native void nativeWrite(int fd, byte[] data, int off, int len);
    private static native void nativeWriteByte(int fd, int b);
    
    @Override public void write(int b) { nativeWriteByte(fd, b); }
    @Override public void write(byte[] buf, int off, int len) { nativeWrite(fd, buf, off, len); }
    
    private void writeString(String s) {
        if (s == null) { writeString("null"); return; }
        // Convert to bytes without using Charset (which needs ICU)
        int len = s.length();
        byte[] buf = new byte[len]; // ASCII assumption for simplicity
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            buf[i] = (byte)(c < 128 ? c : '?');
        }
        write(buf, 0, len);
    }
    
    @Override public void println(String s) { writeString(s); write('\n'); }
    @Override public void print(String s) { writeString(s); }
    @Override public void println(Object o) { println(String.valueOf(o)); }
    @Override public void println(int i) { println(String.valueOf(i)); }
    @Override public void println(long l) { println(String.valueOf(l)); }
    @Override public void println(boolean b) { println(String.valueOf(b)); }
    @Override public void println() { write('\n'); }
    @Override public void flush() {}
    @Override public void close() {}
}

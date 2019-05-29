package io.penguinstats.service;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

public interface CLibrary extends Library {
    CLibrary INSTANCE = (CLibrary) Native.loadLibrary((Platform.isWindows() ? "msvcrt" : "c"), CLibrary.class);

    int LC_CTYPE=0;
    int LC_NUMERIC=1;
    int LC_ALL=6;

    // char *setlocale(int category, const char *locale);
    String setlocale(int category, String locale);
}

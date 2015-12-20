package com.oczeretko.dsbforsinket.utils;

public interface Func<Source, Result> {
    Result invoke(Source item);
}


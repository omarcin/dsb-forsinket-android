package com.oczeretko.dsbforsinket.utils;

public interface Action<Source> {
    void invoke(Source arg);
}

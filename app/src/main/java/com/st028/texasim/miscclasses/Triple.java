package com.st028.texasim.miscclasses;

/**
 * Created by sam on 7/25/14.
 */
public class Triple<F, S, T> {

    public final F first;
    public final S second;
    public final T third;

    public Triple(F f, S s, T t) {
        first = f;
        second = s;
        third = t;
    }

}

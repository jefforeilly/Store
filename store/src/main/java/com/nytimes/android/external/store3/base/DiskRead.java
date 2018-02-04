package com.nytimes.android.external.store3.base;

import javax.annotation.Nonnull;

import io.reactivex.Maybe;

public interface DiskRead<Parsed, Key> {
    @Nonnull
    Maybe<Parsed> read(@Nonnull Key key);
}

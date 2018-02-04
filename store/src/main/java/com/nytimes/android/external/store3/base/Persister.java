package com.nytimes.android.external.store3.base;

import javax.annotation.Nonnull;

import io.reactivex.Maybe;
import io.reactivex.Single;

/**
 * Interface for fetching data from persister
 * when implementing also think about implementing PathResolver to ease in creating primary keys
 *
 * @param <Parsed> data type after parsing
 */
public interface Persister<Parsed, Key> extends DiskRead<Parsed, Key>, DiskWrite<Parsed, Key> {

    /**
     * @param key to use to get data from persister
     *                If data is not available implementer needs to
     *                either return Observable.empty or throw an exception
     */
    @Override
    @Nonnull
    Maybe<Parsed> read(@Nonnull final Key key);

    /**
     * @param key to use to store data to persister
     * @param parsed     raw string to be stored
     */
    @Override
    @Nonnull
    Single<Boolean> write(@Nonnull final Key key, @Nonnull final Parsed parsed);
}

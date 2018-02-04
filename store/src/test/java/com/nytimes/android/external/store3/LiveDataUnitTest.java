package com.nytimes.android.external.store3;

import com.nytimes.android.external.store3.base.Fetcher;
import com.nytimes.android.external.store3.base.Parser;
import com.nytimes.android.external.store3.base.Persister;
import com.nytimes.android.external.store3.base.impl.BarCode;
import com.nytimes.android.external.store3.base.impl.StoreBuilder;
import com.nytimes.android.external.store3.util.ParserException;

import org.junit.Test;

import java.util.HashMap;
import java.util.concurrent.Callable;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.BooleanSupplier;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class LiveDataUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testResubscribe(){
        Observable<String> observable = Observable.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "daaaas";
            }
        });
        observable.subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                System.out.println("ok1:" + s);
            }
        });
        observable.subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                System.out.println("ok2:" + s);
            }
        });
    }

    @Test
    public void testLiveData() {
        final BarCode barCode = new BarCode(Data.class.getName(), "aaa");
        HashMap<String, String> hashMap = new HashMap<>();
        StoreBuilder.<String>barcode()
                .fetcher(new Fetcher<String, BarCode>() {
                    @Override
                    public Single<String> fetch(BarCode barCode) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return Single.just(barCode.getKey() + "");
                    }
                })
                .persister(new Persister<String, BarCode>() {
                    @Override
                    public Maybe<String> read(BarCode barCode) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return Maybe.just(hashMap.get(barCode.getKey()));
                    }

                    @Override
                    public Single<Boolean> write(BarCode barCode, String s) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        hashMap.put(barCode.getKey(), s);
                        return Single.just(new Boolean(true));
                    }
                })
                .parser(new Parser<String, String>() {
                    @Override
                    public String apply(String s) throws ParserException {
                        return "parsed:" + s;
                    }
                })
                .open()
                .get(barCode)
                .repeatUntil(new BooleanSupplier() {
                    @Override
                    public boolean getAsBoolean() throws Exception {
                        Thread.sleep(500);
//                        barCode.setKey(Math.random() * 100 +"" );
                        barCode.setKey("100");
                        return false;
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String o) throws Exception {
                        System.out.println(o.toString());
                    }
                });
        try {
            Thread.sleep(9000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRepeat() {
        Observable.fromCallable(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return Math.random();
            }
        })
                .map(new Function<Object, Object>() {
                    @Override
                    public Object apply(Object o) throws Exception {
                        return Double.parseDouble(o + "") * 100;
                    }
                })
                .repeatUntil(new BooleanSupplier() {
                    @Override
                    public boolean getAsBoolean() throws Exception {
                        Thread.sleep(500);
                        return false;
                    }
                }).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                System.out.println(o);
            }
        });
    }

    class Data {
        public int value = 3;
        public String name = "kkkk";
    }
}
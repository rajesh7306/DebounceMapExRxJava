package com.example.rxdebounce;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

public class MainActivity extends AppCompatActivity {

    private static final String TAG =MainActivity.class.getSimpleName();


    @BindView(R.id.tap_result)
    TextView txtTapResult;

    @BindView(R.id.tap_result_max_count)
    TextView txtTapResultMax;

    @BindView(R.id.layout_tap_area)
    Button btnTapArea;

    private Disposable disposable;
    private Unbinder unbinder;
    private int maxTaps = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        unbinder = ButterKnife.bind(this);
        
         /**
         * here buffer(3) is used
         * it emits 3 integers at a time.
         */

        /**
         * here buffer(3) is used
         * it emits 3 integers at a time.
         */

        RxView.clicks(btnTapArea)
                .map(new Function<Object, Integer>() {
                    @Override
                    public Integer apply(Object o) throws Exception {
                        return 1;
                    }
                })
                .buffer(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new Observer<List<Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(List<Integer> integers) {
                        Log.e(TAG, "onNext: " + integers.size() + " taps received!");
                        if (integers.size() > 0) {
                            maxTaps = integers.size() > maxTaps ? integers.size() : maxTaps;
                            txtTapResult.setText(String.format("Received %d taps in 3 secs", integers.size()));
                            txtTapResultMax.setText(String.format("Maximum of %d taps received in this session", maxTaps));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete");
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        disposable.dispose();
    }

    Observable<User> userObservable = getUsersObservable();

    /**
     * In the filter() method,
     * each user is checked against female gender by user.getGender().equalsIgnoreCase(“female”) condition.
     */

userObservable
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
            .filter(new Predicate<User>() {
        @Override
        public boolean test(User user) throws Exception {
            return user.getGender().equalsIgnoreCase("female");
        }
    })
            .subscribeWith(new DisposableObserver<User>() {
        @Override
        public void onNext(User user) {
            Log.e(TAG, user.getName() + ", " + user.getGender());
        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onComplete() {

        }
    });

    private Observable<User> getUsersObservable() {
        String[] maleUsers = new String[]{"Mark", "John", "Trump", "Obama"};
        String[] femaleUsers = new String[]{"Lucy", "Scarlett", "April"};

        final List<User> users = new ArrayList<>();

        for (String name : maleUsers) {
            User user = new User();
            user.setName(name);
            user.setGender("male");

            users.add(user);
        }

        for (String name : femaleUsers) {
            User user = new User();
            user.setName(name);
            user.setGender("female");

            users.add(user);
        }
        return Observable
                .create(new ObservableOnSubscribe<User>() {
                    @Override
                    public void subscribe(ObservableEmitter<User> emitter) throws Exception {
                        for (User user : users) {
                            if (!emitter.isDisposed()) {
                                emitter.onNext(user);
                            }
                        }

                        if (!emitter.isDisposed()) {
                            emitter.onComplete();
                        }
                    }
                }).subscribeOn(Schedulers.io());
    }

    public class User {
        String name;
        String gender;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }
    }

    /**
     *  if skip(4) is operator is used,
     *  it skips 1-4 and emits the numbers 5, 6, 7, 8, 9, 10.
     */

    Observable
            .range(1, 10)
            .skip(4)
        .subscribe(new Observer<Integer>() {
        @Override
        public void onSubscribe(Disposable d) {
            Log.d(TAG, "Subscribed");
        }

        @Override
        public void onNext(Integer integer) {
            Log.d(TAG, "onNext: " + integer);
        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onComplete() {
            Log.d(TAG, "Completed");
        }
    });

    /**
     * skipLast(4) skips the emission of 7-10 and emits only 1, 2, 3, 4, 5, 6
     */

    Observable
            .range(1, 10)
            .skipLast(4)
        .subscribe(new Observer<Integer>() {
        @Override
        public void onSubscribe(Disposable d) {
            Log.d(TAG, "Subscribed");
        }

        @Override
        public void onNext(Integer integer) {
            Log.d(TAG, "onNext: " + integer);
        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onComplete() {
            Log.d(TAG, "Completed");
        }
    });

    /**
     *  take(4) takes first 4 emissions i.e 1, 2, 3, 4 and skips the remaining.
     *  Range() creates an Observable from a sequence of generated integers.
     *  The function generates sequence of integers by taking starting number and length.
     */

    Observable
            .range(1, 10)
            .take(4)
       .subscribe(new Observer<Integer>() {
        @Override
        public void onSubscribe(Disposable d) {
            Log.d(TAG, "Subscribed");
        }

        @Override
        public void onNext(Integer integer) {
            Log.d(TAG, "onNext: " + integer);
        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onComplete() {
            Log.d(TAG, "Completed");
        }
    });

    Observable<Integer> numbersObservable = Observable.just(10,10, 15, 20, 100, 200, 100, 300, 20, 100);

    /**
     *  Using distinct(), emission of duplicates can be avoided.
     */

        numbersObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
            .distinct()
                .subscribe(new Observer<Integer>() {
        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onNext(Integer integer) {
            Log.d(TAG, "onNext: " + integer);
        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onComplete() {

        }
    });

    /**
     *  repeats the emission of integers from 1-4 three times using repeat(3).
     */

        Observable
                .range(1, 4)
                .repeat(3)
         .subscribe(new Observer<Integer>() {
        @Override
        public void onSubscribe(Disposable d) {
            Log.d(TAG, "Subscribed");
        }

        @Override
        public void onNext(Integer integer) {
            Log.d(TAG, "onNext: " + integer);
        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onComplete() {
            Log.d(TAG, "Completed");
        }
    });

    /**
     * reduce() operator calculates the sum of all the numbers and emits the final result.
     */

        Observable
                .range(1, 10)
                .reduce(new BiFunction<Integer, Integer, Integer>() {
        @Override
        public Integer apply(Integer number, Integer sum) throws Exception {
            return sum + number;
        }
    })
            .subscribe(new MaybeObserver<Integer>() {
        @Override
        public void onSubscribe(Disposable d) {
            disposable = d;
        }

        @Override
        public void onSuccess(Integer integer) {
            Log.e(TAG, "Sum of numbers from 1 - 10 is: " + integer);
        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, "onError: " + e.getMessage());
        }

        @Override
        public void onComplete() {
            Log.e(TAG, "onComplete");
        }
    });

    /**
     * emits the max value of an integer series.
     */

    Integer[] numbers = {5, 101, 404, 22, 3, 1024, 65};

    Observable<Integer> observable = Observable.from(numbers);

MathObservable
        .max(observable)
            .subscribe(new Subscriber<Integer>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(Integer integer) {
            Log.d(TAG, "Max value: " + integer);
        }
    });

    /**
     * Min() operator emits the minimum valued item in the Observable data set.
     */

    Integer[] numbers = {5, 101, 404, 22, 3, 1024, 65};

    Observable<Integer> observable = Observable.from(numbers);

MathObservable
        .min(observable)
            .subscribe(new Subscriber<Integer>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(Integer integer) {
            Log.d(TAG, "Min value: " + integer);
        }
    });

    /**
     * Calculates the sum of all the items emitted by an Observable and emits only the Sum value
     */

    Integer[] numbers = {5, 101, 404, 22, 3, 1024, 65};

    Observable<Integer> observable = Observable.from(numbers);

MathObservable
        .sumInteger(observable)
            .subscribe(new Subscriber<Integer>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(Integer integer) {
            Log.d(TAG, "Min value: " + integer);
        }
    });

}

package com.github.filipebezerra.stackoverflowapi.network.http;

import android.util.Log;
import com.github.filipebezerra.stackoverflowapi.network.utils.NetworkUtil;
import com.github.filipebezerra.stackoverflowapi.stackoverflow.models.Question;
import com.github.filipebezerra.stackoverflowapi.stackoverflow.models.StackOverflowQuestions;
import com.github.filipebezerra.stackoverflowapi.stackoverflow.services.StackOverflowApiService;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;
import java.io.IOException;
import java.util.List;
import retrofit.Callback;
import retrofit.Converter;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 05/11/2015
 * @since #
 */
public class RetrofitAsyncCall implements AsyncCall<List<Question>> {
    private static final String TAG = RetrofitAsyncCall.class.getSimpleName();
    private final Converter.Factory mConverter;

    public RetrofitAsyncCall(Converter.Factory converter) {
        mConverter = converter;
    }

    @Override
    public void execute(String tagged, final AsyncLifecycle<List<Question>> lifecycle) {
        Log.d(TAG, "creating Interceptor");
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        Log.d(TAG, "creating HttpClient");
        final OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.interceptors().add(interceptor);

        Log.d(TAG, "creating Retrofit");
        final Retrofit retrofit = new Retrofit.Builder().
                baseUrl(NetworkUtil.BASE_URL).
                addConverterFactory(mConverter).
                client(okHttpClient).
                build();

        Log.d(TAG, "onBeforeExecute()");
        lifecycle.onBeforeExecute();

        retrofit.create(StackOverflowApiService.class).
                searchQuestions(tagged).
                enqueue(new Callback<StackOverflowQuestions>() {
                    @Override
                    public void onResponse(Response<StackOverflowQuestions> response,
                            Retrofit retrofit) {
                        Log.d(TAG, "message: "+response.message());
                        Log.d(TAG, "code: "+String.valueOf(response.code()));
                        Log.d(TAG, "headers: "+response.headers().toString());

                        if (response.isSuccess()) {
                            if (response.body().items.isEmpty()) {
                                lifecycle.onResultNothing();
                            } else {
                                lifecycle.onSuccess(response.body().items);
                            }
                        } else {
                            try {
                                final String errorString = response.errorBody().string();
                                Log.d(TAG, errorString);
                                lifecycle.onFailure(new Exception(errorString));
                            } catch (IOException e) {
                                Log.e(TAG, "Decoding the response body as UTF-8", e);
                                lifecycle.onFailure(e);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.e(TAG, "onFailure()", t);
                        lifecycle.onFailure(t);
                    }
                });
    }
}

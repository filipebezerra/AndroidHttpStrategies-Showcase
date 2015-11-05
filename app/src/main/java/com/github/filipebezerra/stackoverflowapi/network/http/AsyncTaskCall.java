package com.github.filipebezerra.stackoverflowapi.network.http;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import com.github.filipebezerra.stackoverflowapi.network.utils.NetworkUtil;
import com.github.filipebezerra.stackoverflowapi.stackoverflow.models.Question;
import com.github.filipebezerra.stackoverflowapi.stackoverflow.models.StackOverflowQuestions;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 05/11/2015
 * @since #
 */
public class AsyncTaskCall implements AsyncCall<List<Question>> {
    private static final String TAG = AsyncTaskCall.class.getSimpleName();

    @Override
    public void execute(String tagged, final AsyncLifecycle<List<Question>> lifecycle) {
        new AsyncTask<String, Void, List<Question>>() {
            private Exception mException;

            @Override
            protected void onPreExecute() {
                Log.d(TAG, "onPreExecute()");
                lifecycle.onBeforeExecute();
            }

            @Override
            protected List<Question> doInBackground(String... params) {
                Log.d(TAG, "doInBackground()");

                if (params.length != 1 || TextUtils.isEmpty(params[0])) {
                    Log.d(TAG, "Must receive the url as argument in the param[0]");
                    mException = new Exception("Must receive the url as argument in the param[0]");
                    return null;
                }
                Log.d(TAG, "param tagged: "+params[0]);

                BufferedReader bufferedReader = null;
                //JsonReader jsonReader = null;
                StackOverflowQuestions stackOverflowQuestions;

                try {
                    final URL url = new URL(params[0]);
                    final HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    bufferedReader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()));
                    final StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }

                    Log.d(TAG, stringBuilder.toString());
                    stackOverflowQuestions = new Gson().
                            fromJson(stringBuilder.toString(), StackOverflowQuestions.class);

                    /*
                    jsonReader = new JsonReader(
                            new InputStreamReader(connection.getInputStream()));
                    jsonReader.beginObject();
                    stackOverflowQuestions = new Gson().fromJson(jsonReader,
                            StackOverflowQuestions.class);
                    jsonReader.endObject();
                    */

                    if (stackOverflowQuestions != null) {
                        return stackOverflowQuestions.items;
                    } else {
                        return null;
                    }
                } catch (IOException e) {
                    if (e instanceof MalformedURLException) {
                        Log.e(TAG, "The url is invalid", e);
                    } else {
                        Log.e(TAG, "IO error opening connection or reading the input streaming", e);
                    }
                    mException = e;
                    return null;
                } finally {
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (IOException e) {
                            Log.e(TAG, "Closing the buffer", e);
                        }
                    }

                    /*
                    if (jsonReader != null) {
                        try {
                            jsonReader.close();
                        } catch (IOException e) {
                            Log.e("StackOverflow Questions", "SearchAsyncTask closing buffer", e);
                        }
                    }
                    */
                }
            }

            @Override
            protected void onPostExecute(List<Question> questions) {
                Log.d(TAG, "onPostExecute()");

                if (mException != null) {
                    lifecycle.onFailure(mException);
                } else {
                    if (questions == null || questions.isEmpty()) {
                        lifecycle.onResultNothing();
                    } else {
                        lifecycle.onSuccess(questions);
                    }
                }
            }
        }.execute(NetworkUtil.buildUrl(tagged));
    }
}

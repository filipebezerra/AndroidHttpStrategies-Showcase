package com.github.filipebezerra.stackoverflowapi.network.utils;

import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * Network utility methods.
 *
 * @author Filipe Bezerra
 * @version #, 05/11/2015
 * @since #
 */
public class NetworkUtil {
    public static final String BASE_URL = "https://api.stackexchange.com/2.2";
    public static final String FULL_URL = BASE_URL + "/search?order=desc&sort=activity&site=stackoverflow";

    /**
     * Build the {@link #FULL_URL} including the "tagged" query parameter.
     *
     * @param query the "tagged" param argument to be appended in the final url
     * @return String representation of the built url
     */
    public static String buildUrl(@NonNull final String query) {
        final Uri uri = Uri.parse(FULL_URL).
                buildUpon().
                appendQueryParameter("tagged", query).
                build();

        return uri.toString();
    }
}

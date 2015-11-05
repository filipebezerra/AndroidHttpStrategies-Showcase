package com.github.filipebezerra.stackoverflowapi.providers;

import android.content.SearchRecentSuggestionsProvider;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 04/11/2015
 * @since #
 */
public class SearchSuggestionsProvider extends SearchRecentSuggestionsProvider {
    public static final String AUTHORITY = SearchSuggestionsProvider.class.getName();
    public static final int MODE = SearchRecentSuggestionsProvider.DATABASE_MODE_QUERIES;

    public SearchSuggestionsProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}

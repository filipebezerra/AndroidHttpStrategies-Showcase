package com.github.filipebezerra.stackoverflowapi.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.github.filipebezerra.stackoverflowapi.stackoverflow.models.Question;
import java.util.ArrayList;
import java.util.List;
import org.unbescape.html.HtmlEscape;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 04/11/2015
 * @since #
 */
public class StackOverflowQuestionsAdapter
        extends RecyclerView.Adapter<StackOverflowQuestionsAdapter.StackOverflowQuestionsViewHolder> {
    private List<Question> mQuestions = new ArrayList<>();

    @Override
    public StackOverflowQuestionsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        //TODO: create customized layout (use Roboto font)

        return new StackOverflowQuestionsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StackOverflowQuestionsViewHolder holder, int position) {
        final Question question = mQuestions.get(position);
        holder.title.setText(HtmlEscape.unescapeHtml(question.title));
        holder.link.setText(question.link);
        Linkify.addLinks(holder.link, Linkify.WEB_URLS);
    }

    @Override
    public int getItemCount() {
        return mQuestions.size();
    }

    public void swapData(@NonNull final List<Question> questions) {
        mQuestions = questions;
        notifyDataSetChanged();
    }

    class StackOverflowQuestionsViewHolder extends RecyclerView.ViewHolder {
        @Bind(android.R.id.text1) TextView title;
        @Bind(android.R.id.text2) TextView link;

        public StackOverflowQuestionsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

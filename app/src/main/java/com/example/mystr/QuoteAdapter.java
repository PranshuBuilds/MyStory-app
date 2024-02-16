package com.example.mystr;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class QuoteAdapter extends RecyclerView.Adapter<QuoteAdapter.ViewHolder> {

        private List<String> mQuotes;
        private int[] mBackgrounds = {R.drawable.gradient2, R.drawable.gradient1, R.drawable.gradient3};

        public QuoteAdapter(List<String> quotes) {
            mQuotes = quotes;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public TextView textViewQuote;
            public LinearLayout layout;

            public ViewHolder(View itemView) {
                super(itemView);
                textViewQuote = itemView.findViewById(R.id.textViewQuote);
                layout = itemView.findViewById(R.id.linearquote);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quote, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String quote = mQuotes.get(position);
            holder.textViewQuote.setText(quote);

            // Set background based on position
            int backgroundResId = mBackgrounds[position % mBackgrounds.length];
            holder.layout.setBackgroundResource(backgroundResId);
        }

        @Override
        public int getItemCount() {
            return mQuotes.size();
        }
    }
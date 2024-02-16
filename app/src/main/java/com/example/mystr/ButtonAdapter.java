package com.example.mystr;

import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ButtonAdapter extends RecyclerView.Adapter<ButtonAdapter.ViewHolder> {

    private List<String> buttonList;
    private OnButtonClickListener listener;
    private int selectedItem = -1;
    public interface OnButtonClickListener {
        void onButtonClick(int position);
    }

    public ButtonAdapter(List<String> buttonList, OnButtonClickListener listener) {
        this.buttonList = buttonList;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewButton;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewButton = itemView.findViewById(R.id.textViewButton);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.button_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.textViewButton.setText(buttonList.get(position));
        // Set background color based on selection
        if (position == selectedItem) {
            holder.textViewButton.setBackgroundResource(R.drawable.buttonbackgroundselected);

// Change to your desired background color
        } else {
            holder.textViewButton.setBackgroundResource(R.drawable.buttonbackground);
        }

        holder.itemView.setOnClickListener(view ->{
                listener.onButtonClick(position);

            // Update the selected item
            selectedItem = position;
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return buttonList.size();
    }
}


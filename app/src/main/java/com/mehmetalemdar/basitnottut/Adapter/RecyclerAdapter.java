package com.mehmetalemdar.basitnottut.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mehmetalemdar.basitnottut.Model.NoteModel;
import com.mehmetalemdar.basitnottut.R;

import java.util.ArrayList;
import java.util.Collection;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RowHolder> implements Filterable {

    private ArrayList<NoteModel> noteModelList;
    private ArrayList<NoteModel> noteModelAll;
    private RowHolder.Listener listener;

    public RecyclerAdapter(ArrayList<NoteModel> noteModelList, RowHolder.Listener listener1) {
        this.noteModelList = noteModelList;
        noteModelAll = new ArrayList<>(noteModelList);

        this.listener = listener1;

    }

    @NonNull
    @Override
    public RowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recycler_row,parent,false);

        return new RowHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RowHolder holder, int position) {

        holder.bind(noteModelList,position,listener);
    }

    @Override
    public int getItemCount() {
        return noteModelList.size();
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }
    Filter mFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String mSearchText = constraint.toString().toLowerCase();
            ArrayList<NoteModel> mSearchModelList = new ArrayList<>();
            if (mSearchText.length() == 0 || mSearchText.isEmpty()){
                    mSearchModelList.addAll(noteModelAll);
            }else {
                for (NoteModel noteModel:noteModelAll){
                    if (noteModel.title.toLowerCase().contains(mSearchText)){
                        mSearchModelList.add(noteModel);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = mSearchModelList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            noteModelList.clear();
            noteModelList.addAll((Collection<? extends NoteModel>) results.values);
            notifyDataSetChanged();
        }
    };

    public static class RowHolder extends RecyclerView.ViewHolder {

        TextView titleText;
        TextView noteText;

        public interface Listener{
            void onItemClickListener(int position);
            void onLongItemClickListener(int position);
        }

        public RowHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(ArrayList<NoteModel> getNoteModel, final int position, final Listener getListener){
            titleText = itemView.findViewById(R.id.txtRecyclerTitle);
            noteText = itemView.findViewById(R.id.txtRecyclerNote);

            titleText.setText(getNoteModel.get(position).title);
            noteText.setText(getNoteModel.get(position).note);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getListener.onItemClickListener(position);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    getListener.onLongItemClickListener(position);
                    return true;
                }
            });

        }
    }
}

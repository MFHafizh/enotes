package id.ac.ui.cs.mobileprogramming.muhammadfakhruddinhafizh.enotes.adapter;

import android.app.Application;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import id.ac.ui.cs.mobileprogramming.muhammadfakhruddinhafizh.enotes.R;
import id.ac.ui.cs.mobileprogramming.muhammadfakhruddinhafizh.enotes.models.Note;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.BeanHolder> {

    private List<Note> list;
    private Context context;
    private LayoutInflater layoutInflater;
    private OnNoteItemClick onNoteItemClick;

    public NotesAdapter(List<Note> list,Context context) {
        layoutInflater = LayoutInflater.from(context);
        this.list = list;
        this.context = context;
        this.onNoteItemClick = (OnNoteItemClick) context;
    }


    @Override
    public BeanHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.note_list_item,parent,false);
        return new BeanHolder(view);
    }

    @Override
    public void onBindViewHolder(BeanHolder holder, int position) {
        Log.e("bind", "onBindViewHolder: "+ list.get(position));
        boolean isTitleEmpty = TextUtils.isEmpty(list.get(position).getTitle());
        boolean isContentEmpty = TextUtils.isEmpty(list.get(position).getContent());
        String title = list.get(position).getTitle();
        String content = list.get(position).getContent();
        holder.textViewTitle.setText(title);
        holder.textViewContent.setText(content);
        String imagePath = list.get(position).getImagePath();
        holder.textViewTitle.setVisibility(isTitleEmpty ? View.GONE : View.VISIBLE);
        holder.textViewContent.setVisibility(isContentEmpty ? View.GONE : View.VISIBLE);
        if (imagePath != null) {
            holder.notesImage.setImageBitmap(BitmapFactory.decodeFile(imagePath));
            holder.notesImage.setVisibility(View.VISIBLE);
        }
        else {
            if (isTitleEmpty) {
                holder.textViewContent.setMaxLines(17);
            }
            else {
                holder.textViewContent.setMaxLines(15);
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class BeanHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewContent;
        TextView textViewTitle;
        ImageView notesImage;
        public BeanHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            textViewContent = itemView.findViewById(R.id.item_text);
            textViewTitle = itemView.findViewById(R.id.tv_title);
            notesImage = itemView.findViewById(R.id.notesImage);
        }

        @Override
        public void onClick(View view) {
            onNoteItemClick.onNoteClick(getAdapterPosition());
        }
    }

    public interface OnNoteItemClick{
        void onNoteClick(int pos);
    }
}
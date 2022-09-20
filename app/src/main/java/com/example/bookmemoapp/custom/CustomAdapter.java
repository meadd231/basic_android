package com.example.bookmemoapp.custom;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookmemoapp.Memo;
import com.example.bookmemoapp.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

import static com.example.bookmemoapp.MainActivity.main_text;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> implements Filterable {
    private ArrayList<Book> filteredList;
    private ArrayList<Book> unFilteredList;
    private Context context;

    public CustomAdapter(Context context, ArrayList<Book> arr) {
        filteredList = arr;
        unFilteredList = arr;
        this.context = context;
    }


    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnCreateContextMenuListener, View.OnClickListener{
        TextView title;
        TextView author;
        TextView firstMemo;
        TextView date;

        public ViewHolder(@NonNull View view) {
            super(view);

            title = view.findViewById(R.id.title_list);
            firstMemo = view.findViewById(R.id.first_memo);
            date = view.findViewById(R.id. date_list);

            view.setOnCreateContextMenuListener(this); //2. 리스너 등록
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getCurrentPosition(getAdapterPosition());
            Intent intent = new Intent(context, Memo.class);
            intent.putExtra("bookIndex", position);
            context.startActivity(intent);
        }

        // 컨텍스트 메뉴에서 수정, 삭제가 가능 하게 함
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem Edit = menu.add(Menu.NONE, 1001, 1, "편집");
            MenuItem Delete = menu.add(Menu.NONE, 1002, 2, "삭제");
            Edit.setOnMenuItemClickListener(onEditMenu);
            Delete.setOnMenuItemClickListener(onEditMenu);
        }

        // 4. 캔텍스트 메뉴 클릭시 동작을 설정
        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {


                switch (item.getItemId()) {
                    case 1001:
                        int position = getCurrentPosition(getAdapterPosition());

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        View view = LayoutInflater.from(context)
                                .inflate(R.layout.add_book_dialog, null, false);
                        builder.setView(view);
                        final Button ButtonSubmit = view.findViewById(R.id.button_dialog_submit);
                        final EditText editTextTitle = view.findViewById(R.id.edittext_dialog_title);
                        final EditText editTextAuthor = view.findViewById(R.id.edittext_dialog_author);
                        final TextView editTextDate = view.findViewById(R.id.edittext_dialog_date);

                        editTextTitle.setText(unFilteredList.get(position).getTitle());
                        editTextAuthor.setText(unFilteredList.get(position).getAuthor());
                        editTextDate.setText(unFilteredList.get(position).getDate());

                        final AlertDialog dialog = builder.create();
                        ButtonSubmit.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                String strTitle = editTextTitle.getText().toString();
                                String strAuthor = editTextAuthor.getText().toString();
                                String strDate = editTextDate.getText().toString();

                                Book book = new Book(strTitle, strAuthor, unFilteredList.get(position).getFirst_memo(),
                                        strDate, unFilteredList.get(position).getMemos());

                                unFilteredList.set(position, book);
                                filteredList.set(getAdapterPosition(), book);
                                notifyItemChanged(position);
                                notifyItemChanged(getAdapterPosition());

                                dialog.dismiss();
                            }
                        });

                        dialog.show();

                        break;

                    case 1002:
                        int position2 = getCurrentPosition(getAdapterPosition());
                        unFilteredList.remove(position2);
                        notifyItemRemoved(position2);
                        notifyItemRangeChanged(position2, unFilteredList.size());
                        notifyItemRangeChanged(getAdapterPosition(), filteredList.size());
                        main_text.setText(filteredList.size()+"권의 책 기록");

                        break;

                }
                return true;
            }
        };
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_book, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.title.setText(filteredList.get(position).getTitle());
        holder.firstMemo.setText(filteredList.get(position).getFirst_memo());
        if (filteredList.get(position).getFirst_memo().equals("")) {
            holder.firstMemo.setVisibility(View.GONE);
        } else {
            holder.firstMemo.setVisibility(View.VISIBLE);
        }
        holder.date.setText(filteredList.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return (null != filteredList ? filteredList.size() : 0);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if(charString.isEmpty()) {
                    filteredList = unFilteredList;
                } else {
                    ArrayList<Book> filteringList = new ArrayList<>();
                    for(Book book : unFilteredList) {
                        if(book.getTitle().toLowerCase().contains(charString.toLowerCase())
                                || book.getAuthor().toLowerCase().contains(charString.toLowerCase())
                                || book.getFirst_memo().toLowerCase().contains(charString.toLowerCase())
                                || book.getDate().toLowerCase().contains(charString.toLowerCase())) {
                            filteringList.add(book);
                        }
                    }
                    filteredList = filteringList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                main_text.setText(filteredList.size()+"권의 책 기록");
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (ArrayList<Book>)results.values;
                notifyDataSetChanged();
            }
        };
    }

    public int getCurrentPosition(int position) {
        int currentPosition = position;
        String checkTitle = filteredList.get(currentPosition).getTitle();
        String checkAuthor = filteredList.get(currentPosition).getAuthor();
        String checkFirstMemo = filteredList.get(currentPosition).getFirst_memo();
        String checkDate = filteredList.get(currentPosition).getDate();
        for (int i=0 ; i <unFilteredList.size() ; i++ ){
            if(checkTitle.equals(unFilteredList.get(i).getTitle())
                    && checkAuthor.equals(unFilteredList.get(i).getAuthor())
                    && checkFirstMemo.equals(unFilteredList.get(i).getFirst_memo())
                    && checkDate.equals(unFilteredList.get(i).getDate())) {
                currentPosition = i;
                break;
            }
        }
        return currentPosition;
    }

}


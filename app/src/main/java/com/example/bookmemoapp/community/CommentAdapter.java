package com.example.bookmemoapp.community;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookmemoapp.R;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    ArrayList<Comment> commentArrayList;
    Context context;

    public CommentAdapter(Context context, ArrayList<Comment> arr) {
        this.commentArrayList = arr;
        this.context = context;
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView commentAuthor;
        TextView commentText;
        TextView commentDate;
        Button commentDeleteButton;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            commentAuthor = itemView.findViewById(R.id.comment_author);
            commentText = itemView.findViewById(R.id.comment_text);
            commentDate = itemView.findViewById(R.id.comment_date);
            commentDeleteButton = itemView.findViewById(R.id.comment_delete_button);

            commentDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    View view = LayoutInflater.from(context)
                            .inflate(R.layout.dialog_check_password, null, false);
                    builder.setView(view);


                    final Button passwordCancelButton = view.findViewById(R.id.password_cancel_button);
                    final Button passwordConfirmButton = view.findViewById(R.id.password_confirm_button);
                    final EditText passwordInputEditText = view.findViewById(R.id.password_input_editText);

                    final AlertDialog dialog = builder.create();


                    // ??????????????? ?????? ????????? ??????
                    passwordConfirmButton.setOnClickListener(new View.OnClickListener() {

                        public void onClick(View v) {

                            String password = commentArrayList.get(getAdapterPosition()).getCommentPassword();
                            String input_code = passwordInputEditText.getText().toString();
                            // 4. ???????????? ????????? ????????? ????????????
                            if (input_code.equals(password)) {
                                Toast.makeText(context, "?????????????????????", Toast.LENGTH_SHORT).show();
                                commentArrayList.remove(getAdapterPosition());
                                dialog.dismiss();
                            } else {
                                Toast.makeText(context, "??????????????? ???????????? ????????????", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    passwordCancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }
            });
        }
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);

        CommentViewHolder viewHolder = new CommentViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        holder.commentAuthor.setText(commentArrayList.get(position).getCommentAuthor());
        holder.commentText.setText(commentArrayList.get(position).getCommentText());
        holder.commentDate.setText(commentArrayList.get(position).getCommentDate());
    }

    @Override
    public int getItemCount() {
        return commentArrayList.size();
    }
}

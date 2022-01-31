package com.ad_victoriam.libtex.admin.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.admin.activities.UserDetailsActivity;
import com.ad_victoriam.libtex.common.models.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    Context context;
    private final List<User> users;

    public UserAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public UserAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserViewHolder holder, int position) {
        holder.constraintLayout.setOnClickListener(view -> clicked(view, position));
        String userFullName = users.get(position).getFullName();
        holder.tFullName.setText(userFullName);
        holder.tEmail.setText(users.get(position).getEmail());
    }

    private void clicked(View view, int position) {
        Intent intent = new Intent(context, UserDetailsActivity.class);
        intent.putExtra("user", users.get(position));
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout constraintLayout;
        TextView tFullName;
        TextView tEmail;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
            tFullName = itemView.findViewById(R.id.tFullName);
            tEmail = itemView.findViewById(R.id.tEmail);
        }
    }
}

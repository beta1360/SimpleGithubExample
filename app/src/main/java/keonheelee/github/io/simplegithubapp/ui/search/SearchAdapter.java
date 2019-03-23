package keonheelee.github.io.simplegithubapp.ui.search;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import keonheelee.github.io.simplegithubapp.R;
import keonheelee.github.io.simplegithubapp.api.Model.GithubRepo;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.RepositoryHolder> {

    private List<GithubRepo> items = new ArrayList<>();
    private ColorDrawable placeholder = new ColorDrawable(Color.GRAY);

    @Nullable
    private ItemClickListener listener;

    @NonNull
    @Override
    public RepositoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RepositoryHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull RepositoryHolder holder, int position) {
        final GithubRepo repo = items.get(position);

        Glide.with(holder.itemView.getContext())
                .load(repo.owner.avartarUrl)
                .placeholder(placeholder)
                .into(holder.ivProfile);

        holder.tvName.setText(repo.fullName);
        holder.tvLanguage.setText(TextUtils.isEmpty(repo.language)
                ? holder.itemView.getContext().getText(R.string.no_language_specified)
                : repo.language);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null)
                    listener.onItemClick(repo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(@NonNull List<GithubRepo> items){
        this.items = items;
    }

    protected void setItemClickListener(@Nullable ItemClickListener listener){
        this.listener = listener;
    }

    protected void clearItems(){
        this.items.clear();
    }

    static class RepositoryHolder extends RecyclerView.ViewHolder {
        ImageView ivProfile;
        TextView tvName;
        TextView tvLanguage;

        RepositoryHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_repository, parent, false));

            ivProfile = itemView.findViewById(R.id.ivItemRepositoryProfile);
            tvName = itemView.findViewById(R.id.tvItemRepositoryName);
            tvLanguage = itemView.findViewById(R.id.tvItemRepositoryLanguage);
        }
    }

    public interface ItemClickListener {
        void onItemClick(GithubRepo repository);
    }
}
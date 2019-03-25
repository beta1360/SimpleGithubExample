package keonheelee.github.io.simplegithubapp.ui.search

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide

import java.util.ArrayList

import keonheelee.github.io.simplegithubapp.R
import keonheelee.github.io.simplegithubapp.api.Model.GithubRepo

class SearchAdapter : RecyclerView.Adapter<SearchAdapter.RepositoryHolder>() {

    private var items: MutableList<GithubRepo> = ArrayList()
    private val placeholder = ColorDrawable(Color.GRAY)
    private var listener: ItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoryHolder {
        return RepositoryHolder(parent)
    }

    override fun onBindViewHolder(holder: RepositoryHolder, position: Int) {
        val repo = items[position]

        Glide.with(holder.itemView.context)
                .load(repo.owner.avartarUrl)
                .placeholder(placeholder)
                .into(holder.ivProfile)

        holder.tvName.text = repo.fullName
        holder.tvLanguage.text = if (TextUtils.isEmpty(repo.language))
            holder.itemView.context.getText(R.string.no_language_specified)
        else
            repo.language

        holder.itemView.setOnClickListener {
            if (listener != null)
                listener!!.onItemClick(repo)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setItems(items: MutableList<GithubRepo>) {
        this.items = items
    }

    fun setItemClickListener(listener: ItemClickListener?) {
        this.listener = listener
    }

    fun clearItems() {
        this.items.clear()
    }

    public class RepositoryHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context)
            .inflate(R.layout.item_repository, parent, false)) {
        var ivProfile: ImageView
        var tvName: TextView
        var tvLanguage: TextView

        init {
            ivProfile = itemView.findViewById(R.id.ivItemRepositoryProfile)
            tvName = itemView.findViewById(R.id.tvItemRepositoryName)
            tvLanguage = itemView.findViewById(R.id.tvItemRepositoryLanguage)
        }
    }

    interface ItemClickListener {
        fun onItemClick(repository: GithubRepo)
    }
}
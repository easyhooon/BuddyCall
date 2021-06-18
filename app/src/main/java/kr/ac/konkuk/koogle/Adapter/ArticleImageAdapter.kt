package kr.ac.konkuk.koogle.Adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.ac.konkuk.koogle.databinding.ItemArticleImageBinding

class ArticleImageAdapter()
    : RecyclerView.Adapter<ArticleImageAdapter.ViewHolder>() {
    private val uriList:ArrayList<Uri> = arrayListOf()

    interface OnItemClickListener {
        fun onItemClick(holder: ViewHolder, uri: Uri)
    }

    fun addItem(uri: Uri) {
        uriList.add(uri)
        notifyDataSetChanged()
    }

    var itemClickListener: OnItemClickListener? = null

    inner class ViewHolder(val binding: ItemArticleImageBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.articleImageView.setOnClickListener {
                itemClickListener?.onItemClick(this, uriList[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ArticleImageAdapter.ViewHolder {
        val view = ItemArticleImageBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return uriList.size
    }

    override fun onBindViewHolder(holder: ArticleImageAdapter.ViewHolder, position: Int) {
        Glide.with(holder.binding.articleImageView)
            .load(uriList[position])
            .into(holder.binding.articleImageView)
        //holder.binding.articleImageView.setImageURI(uriList[position])
    }
}
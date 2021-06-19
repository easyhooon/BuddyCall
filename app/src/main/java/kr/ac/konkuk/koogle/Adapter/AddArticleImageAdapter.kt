package kr.ac.konkuk.koogle.Adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.ac.konkuk.koogle.databinding.ItemAddArticleImageBinding
import kr.ac.konkuk.koogle.databinding.ItemArticleImageBinding

class AddArticleImageAdapter(private val uriList:ArrayList<Uri>)
    : RecyclerView.Adapter<AddArticleImageAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun OnItemClick(holder: ViewHolder)
    }

    var itemClickListener: OnItemClickListener? = null

    fun addItem(uri: Uri) {
        uriList.add(uri)
    }

    fun moveItem(oldPos:Int, newPos:Int) {
        val item = uriList[oldPos]
        uriList.removeAt(oldPos)
        uriList.add(newPos, item)
        notifyItemMoved(oldPos, newPos)
    }

    fun removeItem(pos:Int) {
        uriList.removeAt(pos)
        notifyItemRemoved(pos)
    }

    inner class ViewHolder(val binding: ItemAddArticleImageBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.itemImageView.setOnClickListener {
                itemClickListener?.OnItemClick(this)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddArticleImageAdapter.ViewHolder {
        val view = ItemAddArticleImageBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddArticleImageAdapter.ViewHolder, position: Int) {
        Glide.with(holder.binding.itemImageView)
            .load(uriList[position])
            .into(holder.binding.itemImageView)
    }

    override fun getItemCount(): Int {
        return uriList.size
    }

    fun getUriList(): ArrayList<Uri> {
        return uriList
    }
}
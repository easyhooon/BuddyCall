package kr.ac.konkuk.koogle.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.ac.konkuk.koogle.Model.CardModel
import kr.ac.konkuk.koogle.R

class CardAdapter: ListAdapter<CardModel, CardAdapter.ViewHolder>(diffUtil){

    inner class ViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
        fun bind(cardModel: CardModel){
            view.findViewById<TextView>(R.id.nameTextView).text = cardModel.userName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_card,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object{
        val diffUtil = object : DiffUtil.ItemCallback<CardModel>() {
            override fun areItemsTheSame(oldModel: CardModel, newModel: CardModel): Boolean {
                return oldModel.postId == newModel.postId
            }

            override fun areContentsTheSame(oldModel: CardModel, newModel: CardModel): Boolean {
                return oldModel == newModel
            }
        }
    }
}

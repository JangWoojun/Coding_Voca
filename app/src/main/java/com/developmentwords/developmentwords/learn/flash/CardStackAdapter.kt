package com.developmentwords.developmentwords.learn.flash

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.developmentwords.developmentwords.databinding.CardItemBinding
import com.developmentwords.developmentwords.util.Voca

class CardStackAdapter(private val words:List<Voca>) :
    RecyclerView.Adapter<CardStackAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType:Int) : CardViewHolder {
        val binding = CardItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,false
        )
        return CardViewHolder(binding).also { holder ->
            binding.cardItem.setOnClickListener {
                words.getOrNull(holder.adapterPosition)?.chk = !words[holder.adapterPosition].chk
                notifyItemChanged(holder.adapterPosition)
            }
        }
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(words[position])
    }

    override fun getItemCount(): Int {
        return words.size
    }

    class CardViewHolder(private val binding: CardItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(word: Voca){
            binding.titleText.text = word.title
            binding.contextText.text = word.context

            if (word.chk) {
                binding.titleText.visibility = View.INVISIBLE
                binding.contextText.visibility = View.VISIBLE
            } else {
                binding.titleText.visibility = View.VISIBLE
                binding.contextText.visibility = View.INVISIBLE
            }
        }
    }

}
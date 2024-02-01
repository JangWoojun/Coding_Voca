package com.developmentwords.developmentwords.word

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.developmentwords.developmentwords.databinding.WordItemBinding
import com.developmentwords.developmentwords.util.Voca

class WordAdapter (private val words : List<Voca>) :
    RecyclerView.Adapter<WordAdapter.WordViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val binding = WordItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return WordViewHolder(binding).also { holder ->
            binding.wordBox.setOnClickListener {
                words.getOrNull(holder.adapterPosition)?.chk = !words[holder.adapterPosition].chk
                notifyItemChanged(holder.adapterPosition)
            }
        }
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        holder.bind(words[position])
    }

    override fun getItemCount(): Int {
        return words.size
    }

    class WordViewHolder(private val binding: WordItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(word : Voca){
            binding.titleText.text = word.title
            binding.contextText.text = word.context

            if (word.chk) {
                binding.showContextText.visibility = View.GONE
                binding.contextBox.visibility = View.VISIBLE
            } else {
                binding.showContextText.visibility = View.VISIBLE
                binding.contextBox.visibility = View.GONE
            }
        }
    }

}
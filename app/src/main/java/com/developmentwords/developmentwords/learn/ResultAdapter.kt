package com.developmentwords.developmentwords.learn

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.developmentwords.developmentwords.R
import com.developmentwords.developmentwords.databinding.ResultItemBinding
import com.developmentwords.developmentwords.util.Voca

class ResultAdapter (private val words : List<Voca>) :
    RecyclerView.Adapter<ResultAdapter.ResultViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val binding = ResultItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        holder.bind(words[position])
    }

    override fun getItemCount(): Int {
        return words.size
    }

    class ResultViewHolder(private val binding: ResultItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(word: Voca) {
            binding.apply {
                titleText.text = word.title
                contextText.text = word.context
                if (word.chk) {
                    titleText.setTextColor(ContextCompat.getColor(binding.root.context, R.color.point_color))
                    contextText.setTextColor(ContextCompat.getColor(binding.root.context, R.color.point_color))
                } else {
                    titleText.setTextColor(ContextCompat.getColor(binding.root.context, R.color.primary_text_color))
                    contextText.setTextColor(ContextCompat.getColor(binding.root.context, R.color.text_color))
                }
            }

        }
    }
}
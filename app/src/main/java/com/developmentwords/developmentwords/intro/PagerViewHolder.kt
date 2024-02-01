package com.developmentwords.developmentwords.intro

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.developmentwords.developmentwords.R
import com.developmentwords.developmentwords.util.PagerItem

class PagerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val itemImage = itemView.findViewById<ImageView>(R.id.pager_item_image)
    private val itemTitle = itemView.findViewById<TextView>(R.id.title_text)
    private val itemSubject = itemView.findViewById<TextView>(R.id.subject_text)

    fun bindingView(pagerItem: PagerItem) {
        itemImage.setImageResource(pagerItem.image)
        itemTitle.text = pagerItem.title
        itemSubject.text = pagerItem.subject
    }
}
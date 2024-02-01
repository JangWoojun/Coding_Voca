package com.developmentwords.developmentwords.intro

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.developmentwords.developmentwords.R
import com.developmentwords.developmentwords.util.PagerItem

class IntroPagerRecyclerAdapter(private val pagerList: ArrayList<PagerItem>): RecyclerView.Adapter<PagerViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        return PagerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.onboard_item, parent, false))
    }

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        holder.bindingView(pagerList[position])
    }

    override fun getItemCount(): Int {
        return pagerList.size
    }
}
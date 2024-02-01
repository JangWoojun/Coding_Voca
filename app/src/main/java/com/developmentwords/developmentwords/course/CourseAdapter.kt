package com.developmentwords.developmentwords.course

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.developmentwords.developmentwords.R
import com.developmentwords.developmentwords.databinding.CourseItemBinding
import com.developmentwords.developmentwords.util.CourseItem

class CourseAdapter(private val courses: List<CourseItem>) :
    RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    enum class FilterOption {
        ALL,
        TOP,
        PIN
    }

    private var filteredList: List<CourseItem> = courses.toMutableList()
    private var currentFilter: FilterOption = FilterOption.ALL

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val binding = CourseItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return CourseViewHolder(binding).also { handler ->
            binding.courseBox.setOnClickListener {
                val bundle = Bundle()
                val item = filteredList[handler.adapterPosition]
                bundle.putSerializable("courseItem", item)

                val fragment = CourseInnerFragment()
                fragment.arguments = bundle
                parent.findNavController().navigate(R.id.action_course_to_courseInnerFragment, bundle)
            }
        }
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.bind(filteredList[position])
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    fun filter(query: String) {
        filteredList = if (query.isEmpty()) {
            filterByOption(currentFilter)
        } else {
            courses.filter { item ->
                item.title.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }

    fun setFilterOption(option: FilterOption) {
        currentFilter = option
        filteredList = filterByOption(option)
        notifyDataSetChanged()
    }

    private fun filterByOption(option: FilterOption): List<CourseItem> {
        return when (option) {
            FilterOption.ALL -> courses.toList()
            FilterOption.TOP -> courses.filter { it.title.contains("초급") or it.title.contains("일반") }
            FilterOption.PIN -> courses.filter { it.pin }
        }
    }

    class CourseViewHolder(private val binding: CourseItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(course: CourseItem) {
            binding.titleText.text = course.title
            binding.levelText.text = course.level
            binding.wordCountText.text = "단어 ${course.wordCount}개"
            binding.updateDateText.text = "최종 업데이트 ${course.updateDate}"
        }
    }
}

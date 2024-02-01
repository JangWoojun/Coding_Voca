package com.developmentwords.developmentwords.course

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.developmentwords.developmentwords.R
import com.developmentwords.developmentwords.databinding.FragmentCourseBinding
import com.developmentwords.developmentwords.util.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CourseFragment : Fragment() {

    private var _binding: FragmentCourseBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCourseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            CoroutineScope(Dispatchers.IO).launch {
                val db = AppDatabase.getDatabase(requireContext())
                val list = db?.courseItemDao()!!.getCourseItem()
                val adapter = CourseAdapter(list)

                courseList.layoutManager = LinearLayoutManager(context)
                courseList.adapter = adapter

                searchArea.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        adapter.filter(s.toString())
                    }

                    override fun afterTextChanged(s: Editable?) {}
                })

                allButton.setOnClickListener {
                    clickFilter(1)
                    val filterOption: CourseAdapter.FilterOption = CourseAdapter.FilterOption.ALL
                    adapter.setFilterOption(filterOption)
                }

                topButton.setOnClickListener {
                    clickFilter(2)
                    val filterOption: CourseAdapter.FilterOption = CourseAdapter.FilterOption.TOP
                    adapter.setFilterOption(filterOption)
                }

                pinButton.setOnClickListener {
                    clickFilter(3)
                    val filterOption: CourseAdapter.FilterOption = CourseAdapter.FilterOption.PIN
                    adapter.setFilterOption(filterOption)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun clickFilter(number: Int) {
        binding.apply {
            when (number) {
                1 -> {
                    allBackground.setTextColor(Color.parseColor("#FFFFFF"))
                    allBackground.setBackgroundResource(R.drawable.round_main_banner)
                    allBackground.backgroundTintList = ColorStateList
                        .valueOf(ContextCompat.getColor(requireContext(), R.color.point_color))
                    topBackground.background = null
                    pinBackground.background = null
                    topBackground.setTextColor(Color.parseColor("#858597"))
                    pinBackground.setTextColor(Color.parseColor("#858597"))
                }
                2 -> {
                    topBackground.setTextColor(Color.parseColor("#FFFFFF"))
                    topBackground.setBackgroundResource(R.drawable.round_main_banner)
                    topBackground.backgroundTintList = ColorStateList
                        .valueOf(ContextCompat.getColor(requireContext(), R.color.point_color))
                    allBackground.background = null
                    pinBackground.background = null
                    allBackground.setTextColor(Color.parseColor("#858597"))
                    pinBackground.setTextColor(Color.parseColor("#858597"))
                }
                3 -> {
                    pinBackground.setTextColor(Color.parseColor("#FFFFFF"))
                    pinBackground.setBackgroundResource(R.drawable.round_main_banner)
                    pinBackground.backgroundTintList = ColorStateList
                        .valueOf(ContextCompat.getColor(requireContext(), R.color.point_color))
                    allBackground.background = null
                    topBackground.background = null
                    allBackground.setTextColor(Color.parseColor("#858597"))
                    topBackground.setTextColor(Color.parseColor("#858597"))
                }
            }
        }

    }
}
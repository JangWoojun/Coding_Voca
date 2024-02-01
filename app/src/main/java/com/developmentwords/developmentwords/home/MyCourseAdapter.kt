package com.developmentwords.developmentwords.home

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.developmentwords.developmentwords.databinding.MyCourseItemBinding
import com.developmentwords.developmentwords.util.AppDatabase
import com.developmentwords.developmentwords.util.Course
import com.developmentwords.developmentwords.util.ProgressUtil.createLoadingDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyCourseAdapter(private val myCourses: List<Course>): RecyclerView.Adapter<MyCourseAdapter.MyCourseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCourseViewHolder {
        val binding = MyCourseItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return MyCourseViewHolder(binding).also { handler ->
            binding.myCourseItemView.setOnClickListener {
                myCourses[handler.adapterPosition].title

                val loadingDialog = createLoadingDialog(binding.root.context)

                CoroutineScope(Dispatchers.IO).launch {
                    withContext(Dispatchers.Main) {
                        loadingDialog.show()
                    }

                    val db = AppDatabase.getDatabase(binding.root.context)
                    val user = db?.userDao()!!.getUser()

                    user.nowCourse = myCourses[handler.adapterPosition]
                    db.userDao().updateUser(user)

                    withContext(Dispatchers.Main) {
                        loadingDialog.dismiss()
                        Toast.makeText(binding.root.context, "변경 완료!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: MyCourseViewHolder, position: Int) {
        holder.bind(myCourses[position])
    }

    override fun getItemCount(): Int {
        return myCourses.size
    }

    class MyCourseViewHolder(private val binding: MyCourseItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(myCourses : Course){

            binding.apply {
                titleText.text = myCourses.title
                nowWordText.text = myCourses.nowWord.toString()
                maxWordText.text = myCourses.maxWord.toString()
                todayLearnWordProgress.max = myCourses.maxWord
                todayLearnWordProgress.progress = myCourses.nowWord

                when(titleText.text.substring(titleText.text.lastIndexOf(' ') + 1)) {
                    "초급" -> {
                        todayLearnWordProgress.progressTintList =
                            ColorStateList.valueOf(Color.parseColor("#EC7B9C"))
                        myCourseItemView.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFE7EE"))
                        playButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#EC7B9C"))
                    }
                    "중급" -> {
                        todayLearnWordProgress.progressTintList =
                            ColorStateList.valueOf(Color.parseColor("#3D5CFF"))
                        myCourseItemView.backgroundTintList= ColorStateList.valueOf(Color.parseColor("#BAD6FF"))
                        playButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#3D5CFF"))
                    }
                    "고급" -> {
                        todayLearnWordProgress.progressTintList =
                            ColorStateList.valueOf(Color.parseColor("#398A80"))
                        myCourseItemView.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#BAE0DB"))
                        playButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#398A80"))
                    }
                    else -> {
                        todayLearnWordProgress.progressTintList =
                            ColorStateList.valueOf(Color.parseColor("#EC7B9C"))
                        myCourseItemView.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFE7EE"))
                        playButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#EC7B9C"))
                    }

                }

            }
        }
    }
}
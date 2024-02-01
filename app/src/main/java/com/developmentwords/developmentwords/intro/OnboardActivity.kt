package com.developmentwords.developmentwords.intro

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.developmentwords.developmentwords.R
import com.developmentwords.developmentwords.auth.LoginActivity
import com.developmentwords.developmentwords.auth.SignUpActivity
import com.developmentwords.developmentwords.databinding.ActivityOnboardBinding
import com.developmentwords.developmentwords.util.PagerItem


class OnboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardBinding
    private var pagerItemList = ArrayList<PagerItem>()

    private lateinit var introPagerRecyclerAdapter: IntroPagerRecyclerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pagerItemList.add(PagerItem(R.drawable.human5, "코딩을 처음 접한다면?" , "코딩을 처음 입문했지만 어렵고 생소한\n" +
                "단어들이 너무 많으신가요?"))
        pagerItemList.add(PagerItem(R.drawable.human4, "언제든 쉽고" +
                "빠르게 학습!", "코딩 보카를 통하면 언제든 어디서든\n" +
                "학습할 수 있어요!"))
        pagerItemList.add(PagerItem(R.drawable.human3, "수준별 체계적 학습!", "\b수준에 따라 체계적으로 자신의\n" +
                "맞는 코스를 선택하여 학습할 수 있어요!"))

        introPagerRecyclerAdapter = IntroPagerRecyclerAdapter(pagerItemList)

        binding.introViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == 2) {
                    binding.buttonView.visibility = View.VISIBLE
                } else {
                    binding.buttonView.visibility = View.GONE
                }
            }
        })

        binding.introViewPager.apply {
            adapter = introPagerRecyclerAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            binding.dotsIndicator.attachTo(this)
        }

        binding.signUpButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.loginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }
}
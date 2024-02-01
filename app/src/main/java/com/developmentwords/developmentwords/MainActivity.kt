package com.developmentwords.developmentwords

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.developmentwords.developmentwords.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var backPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            val navController = findNavController(R.id.nav_host_fragment)
            bottomNavigation.setupWithNavController(navController)

            bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.home -> navController.navigate(R.id.home)
                    R.id.word -> navController.navigate(R.id.word)
                    R.id.learn -> navController.navigate(R.id.learn)
                    R.id.course -> navController.navigate(R.id.course)
                    R.id.account -> navController.navigate(R.id.account)
                    else -> return@setOnNavigationItemSelectedListener false
                }
                true
            }
        }


    }

    fun hideBottomNavi(state: Boolean) {
        if (state) binding.bottomNavigation.visibility =
            View.GONE else binding.bottomNavigation.visibility = View.VISIBLE
    }

    override fun onBackPressed() {

        val navController = findNavController(R.id.nav_host_fragment)

        if (navController.currentDestination?.id != R.id.home) {
            navController.popBackStack(R.id.home, false)
        } else {
            if (System.currentTimeMillis() - backPressedTime >= 2000) {
                backPressedTime = System.currentTimeMillis()
                Toast.makeText(this, "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
            } else {
                finish()
            }
        }
    }

}
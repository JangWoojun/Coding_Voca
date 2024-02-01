package com.developmentwords.developmentwords.util

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import com.airbnb.lottie.LottieAnimationView
import com.developmentwords.developmentwords.MainActivity
import com.developmentwords.developmentwords.R

object ProgressUtil {

    fun createDialog(context: Context, dialogType: Boolean) {
        val dialogSource: Int = if (dialogType) {
            R.layout.success_custom_dialog
        } else {
            R.layout.fail_custom_dialog
        }

        val dialogView = LayoutInflater.from(context).inflate(dialogSource, null)
        val dialogOkButton = dialogView.findViewById<ImageView>(R.id.move_home_button)

        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)
        val dialog = builder.create()
        dialog.show()

        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window?.attributes)
        layoutParams.width = (context.resources.displayMetrics.widthPixels * 0.8).toInt()
        dialog.window?.attributes = layoutParams

        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)

        if (dialogType) {
            dialogOkButton.setOnClickListener {
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
                dialog.dismiss()
                (context as Activity).finishAffinity()
            }
        } else {
            dialogOkButton.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    fun createLoadingDialog(context: Context): Dialog {
        val dialog = Dialog(context)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.loding_dialog)

        val animationView = dialog.findViewById<LottieAnimationView>(R.id.animation_view)
        animationView.setAnimation("loading.json")
        animationView.loop(true)
        animationView.playAnimation()

        return dialog
    }
}

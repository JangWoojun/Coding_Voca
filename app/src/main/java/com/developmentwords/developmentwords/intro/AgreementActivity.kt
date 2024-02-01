package com.developmentwords.developmentwords.intro

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.widget.CheckBox
import com.developmentwords.developmentwords.R
import com.developmentwords.developmentwords.databinding.ActivityAgreementBinding

class AgreementActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAgreementBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgreementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

            consentCheckBox1.isClickable = false
            consentCheckBox1.isClickable = false

            allCheckBox.setOnClickListener {
                if (allCheckBox.isChecked) {
                    consentCheckBox1.isChecked = true
                    consentCheckBox2.isChecked = true
                    startActivity(Intent(this@AgreementActivity, OnboardActivity::class.java))
                    finish()
                } else {
                    consentCheckBox1.isChecked = false
                    consentCheckBox2.isChecked = false
                }
            }

            consentCheckBox1.setOnClickListener {
                allCheckBox.isChecked = consentCheckBox1.isChecked && consentCheckBox2.isChecked
                if (consentCheckBox1.isChecked) {
                    if (consentCheckBox2.isChecked) {
                        startActivity(Intent(this@AgreementActivity, OnboardActivity::class.java))
                        finish()
                    } else {
                        val dialog = Dialog(this@AgreementActivity)
                        dialog.setContentView(R.layout.webview_dialog)
                        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)
                        val windowWidth = resources.displayMetrics.widthPixels * 0.9
                        val windowHeight = resources.displayMetrics.heightPixels * 0.7
                        dialog.window?.setLayout(windowWidth.toInt(), windowHeight.toInt())

                        val ok = dialog.findViewById<CheckBox>(R.id.yes_button)
                        val x = dialog.findViewById<CheckBox>(R.id.no_button)
                        val webView = dialog.findViewById<WebView>(R.id.webview)

                        webView.loadUrl("https://sch10719.neocities.org/codingvoca2")

                        ok.setOnClickListener {
                            dialog.dismiss()
                            consentCheckBox1.isChecked = true
                        }

                        x.setOnClickListener {
                            dialog.dismiss()
                            consentCheckBox1.isChecked = false
                        }

                        dialog.show()
                    }
                }
            }

            consentCheckBox2.setOnClickListener {
                allCheckBox.isChecked = consentCheckBox2.isChecked && consentCheckBox1.isChecked
                if (consentCheckBox2.isChecked) {
                    if (consentCheckBox1.isChecked) {
                        startActivity(Intent(this@AgreementActivity, OnboardActivity::class.java))
                        finish()
                    } else {
                        val dialog = Dialog(this@AgreementActivity)
                        dialog.setContentView(R.layout.webview_dialog)
                        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)
                        val windowWidth = resources.displayMetrics.widthPixels * 0.9
                        val windowHeight = resources.displayMetrics.heightPixels * 0.7
                        dialog.window?.setLayout(windowWidth.toInt(), windowHeight.toInt())

                        val ok = dialog.findViewById<CheckBox>(R.id.yes_button)
                        val x = dialog.findViewById<CheckBox>(R.id.no_button)
                        val webView = dialog.findViewById<WebView>(R.id.webview)

                        webView.loadUrl("https://sch10719.neocities.org/codingvocaprivacypolicy")

                        ok.setOnClickListener {
                            dialog.dismiss()
                            consentCheckBox2.isChecked = true
                        }

                        x.setOnClickListener {
                            dialog.dismiss()
                            consentCheckBox2.isChecked = false
                        }

                        dialog.show()
                    }
                }
            }

            consentCheck1.setOnClickListener {
                val dialog = Dialog(this@AgreementActivity)
                dialog.setContentView(R.layout.webview_dialog)
                dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)
                val windowWidth = resources.displayMetrics.widthPixels * 0.9
                val windowHeight = resources.displayMetrics.heightPixels * 0.7
                dialog.window?.setLayout(windowWidth.toInt(), windowHeight.toInt())

                val ok = dialog.findViewById<CheckBox>(R.id.yes_button)
                val x = dialog.findViewById<CheckBox>(R.id.no_button)
                val webView = dialog.findViewById<WebView>(R.id.webview)

                webView.loadUrl("https://sch10719.neocities.org/codingvoca2")

                ok.setOnClickListener {
                    dialog.dismiss()
                    consentCheckBox1.isChecked = true
                }

                x.setOnClickListener {
                    dialog.dismiss()
                    consentCheckBox1.isChecked = false
                }

                dialog.show()
            }

            consentCheck2.setOnClickListener {
                val dialog = Dialog(this@AgreementActivity)
                dialog.setContentView(R.layout.webview_dialog)
                dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)
                val windowWidth = resources.displayMetrics.widthPixels * 0.9
                val windowHeight = resources.displayMetrics.heightPixels * 0.7
                dialog.window?.setLayout(windowWidth.toInt(), windowHeight.toInt())

                val ok = dialog.findViewById<CheckBox>(R.id.yes_button)
                val x = dialog.findViewById<CheckBox>(R.id.no_button)
                val webView = dialog.findViewById<WebView>(R.id.webview)

                webView.loadUrl("https://sch10719.neocities.org/codingvocaprivacypolicy")

                ok.setOnClickListener {
                    dialog.dismiss()
                    consentCheckBox2.isChecked = true
                }

                x.setOnClickListener {
                    dialog.dismiss()
                    consentCheckBox2.isChecked = false
                }

                dialog.show()
            }
        }

    }
}
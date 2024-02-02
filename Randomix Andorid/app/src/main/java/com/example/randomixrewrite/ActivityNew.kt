package com.example.randomixrewrite

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.internal.wait


class ActivityNew : AppCompatActivity(){
    private lateinit var editText1: EditText
    private lateinit var editText2: EditText
    private lateinit var editTextResult: CardView
    private lateinit var textViewResult: TextView
    private lateinit var calculateButton: CardView
    private var x1: Float = 0.0f
    private var y1: Float = 0.0f
    private var x2: Float = 0.0f
    private var y2: Float = 0.0f

    private lateinit var weatherData: WeatherData
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private val animatorSet = AnimatorSet()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new)



        latitude = intent.getDoubleExtra("latitude",0.0)
        longitude = intent.getDoubleExtra("longitude",0.0)

        editText1 = findViewById(R.id.editText1)
        editText2 = findViewById(R.id.editText2)
        editTextResult = findViewById(R.id.editTextResult)
        calculateButton = findViewById(R.id.calculateButton)
        textViewResult = findViewById(R.id.textViewResult)

        calculateButton.setOnClickListener {
            animateButton(it)
            hideKeyboard()
            val minText = editText1.text.toString()
            val maxText = editText2.text.toString()

            if (minText.isBlank() || maxText.isBlank()) {
                showToast("Пожалуйста, введите значения для min и max.")
                return@setOnClickListener
            }

            if (!minText.isDigitsOnly() || !maxText.isDigitsOnly()) {
                showToast("Пожалуйста, введите числовые значения для min и max.")
                return@setOnClickListener
            }

            val minValue = minText.toInt()
            val maxValue = maxText.toInt()

            if (minValue >= maxValue) {
                showToast("min должен быть меньше max.")
                return@setOnClickListener
            }

            lifecycleScope.launch {
                weatherData = getWeatherData()

                val randAlgorithm = randAlg(weatherData)
                val result = randAlgorithm.createNumberFromMask(minValue..maxValue)

                textViewResult.text = result.toString()
            }
        }
    }





    override fun onTouchEvent(touchEvent: MotionEvent): Boolean {
        when (touchEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                x1 = touchEvent.x
                y1 = touchEvent.y
            }
            MotionEvent.ACTION_UP -> {
                x2 = touchEvent.x
                y2 = touchEvent.y
                if (x1 > x2) {
                    val i = Intent(this@ActivityNew, Stack::class.java)
                    startActivity(i)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }
            }
        }
        return false
    }

    private fun showToast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private suspend fun getWeatherData(): WeatherData{
        return WeatherManager(this@ActivityNew).getCurrentWeather(latitude,longitude)
    }

    private fun animateButton(view: View) {
        val scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 0.9f)
        val scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 0.9f)
        scaleDownX.duration = 150
        scaleDownY.duration = 150

        scaleDownX.interpolator = AccelerateDecelerateInterpolator()
        scaleDownY.interpolator = AccelerateDecelerateInterpolator()

        val scaleXReverse = ObjectAnimator.ofFloat(view, "scaleX", 1.0f)
        val scaleYReverse = ObjectAnimator.ofFloat(view, "scaleY", 1.0f)
        scaleXReverse.duration = 150
        scaleYReverse.duration = 150

        scaleXReverse.interpolator = AccelerateDecelerateInterpolator()
        scaleYReverse.interpolator = AccelerateDecelerateInterpolator()

        animatorSet.play(scaleDownX).with(scaleDownY)
        animatorSet.play(scaleXReverse).with(scaleYReverse).after(scaleDownX)

        if (!animatorSet.isRunning) {
            animatorSet.start()
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(calculateButton.windowToken, 0)
    }
}
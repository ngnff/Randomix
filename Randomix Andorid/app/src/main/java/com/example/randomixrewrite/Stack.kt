package com.example.randomixrewrite

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import java.util.Stack


class Stack : AppCompatActivity(){
    private lateinit var editText1: EditText
    private lateinit var editText2: EditText
    private lateinit var calculateButton: CardView
    private lateinit var randomValuesStack: Stack<Int>
    private lateinit var stackRecyclerView: RecyclerView
    private lateinit var stackAdapter: StackAdapter




    private var x1: Float = 0.0f
    private var y1: Float = 0.0f
    private var x2: Float = 0.0f
    private var y2: Float = 0.0f


    private lateinit var weatherData: WeatherData
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private val animatorSet = AnimatorSet()



    @SuppressLint("ClickableViewAccessibility", "NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stack2)

        randomValuesStack = Stack()



        latitude = intent.getDoubleExtra("latitude",0.0)
        longitude = intent.getDoubleExtra("longitude",0.0)


        editText1 = findViewById(R.id.editText1)
        editText2 = findViewById(R.id.editText2)
        calculateButton = findViewById(R.id.calculateButton)

        stackRecyclerView = findViewById(R.id.stackRecyclerView)
        stackAdapter = StackAdapter()

        val layoutManager = GridLayoutManager(this, 2)
        stackRecyclerView.layoutManager = layoutManager
        stackRecyclerView.adapter = stackAdapter


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

                randomValuesStack.push(result)
                updateStackRecyclerView()
                Log.d("StackMes", randomValuesStack.toString())

            }
        }


    }

    data class StackItem(val value: Int)

    class StackAdapter : RecyclerView.Adapter<StackAdapter.StackViewHolder>() {

        private var stackItems: List<StackItem> = emptyList()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StackViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_stack_value, parent, false)
            return StackViewHolder(view)
        }

        override fun onBindViewHolder(holder: StackViewHolder, position: Int) {
            holder.bind(stackItems[position])
        }

        override fun getItemCount(): Int = stackItems.size

        fun submitList(items: List<Int>) {
            stackItems = items.map { StackItem(it) }
            notifyDataSetChanged()
        }

        class StackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(item: StackItem) {
                val textView: TextView = itemView.findViewById(R.id.stackItemTextView)
                textView.text = item.value.toString()
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
                if (x1 < x2) {
                    val i = Intent(this@Stack, ActivityNew::class.java)
                    startActivity(i)
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                }
            }
        }
        return false
    }

    private fun showToast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private suspend fun getWeatherData(): WeatherData{
        return WeatherManager(this@Stack).getCurrentWeather(latitude,longitude)
    }

    private fun updateStackRecyclerView() {
        stackAdapter.submitList(randomValuesStack.toList())
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
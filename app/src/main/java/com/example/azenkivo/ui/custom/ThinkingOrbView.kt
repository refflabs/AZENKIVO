package com.example.azenkivo.ui.custom

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/** ThinkingOrbView: bola titik bernapas (lingkaran-lingkaran titik konsentris) */
class ThinkingOrbView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.WHITE
    }

    private var animator: ValueAnimator? = null
    private var animProgress = 0f

    // Posisi titik dihitung sekali saat ukuran view diketahui
    private var titikX = FloatArray(0)
    private var titikY = FloatArray(0)
    private var titikRing = IntArray(0)
    private var ukuranDasar = 0f

    // Warna orb
    var orbColor: Int
        get() = paint.color
        set(value) {
            paint.color = value
            invalidate()
        }

    init {
        startAnimation()
    }

    private fun startAnimation() {
        animator?.cancel()
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 1600L // 1.6 detik siklus napas
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener {
                animProgress = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (animator?.isStarted != true) {
            startAnimation()
        }
    }

    override fun onDetachedFromWindow() {
        animator?.cancel()
        super.onDetachedFromWindow()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredSize = (56 * resources.displayMetrics.density).toInt()
        val width = resolveSize(desiredSize, widthMeasureSpec)
        val height = resolveSize(desiredSize, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val side = minOf(w, h).toFloat()
        val pusatX = w / 2f
        val pusatY = h / 2f
        ukuranDasar = side * 0.05f
        val jariJariMaks = side / 2f - ukuranDasar * 1.1f

        // Ring ke-k berisi 4k titik (4, 8, 12, 16, 20)
        val total = (1..RING_COUNT).sumOf { 4 * it }
        titikX = FloatArray(total)
        titikY = FloatArray(total)
        titikRing = IntArray(total)
        var idx = 0
        for (k in 1..RING_COUNT) {
            val jumlah = 4 * k
            val r = (k - 0.5f) / (RING_COUNT - 0.5f) * jariJariMaks
            // Ring genap digeser setengah jarak supaya pola tidak lurus
            val geser = if (k % 2 == 0) (PI / jumlah).toFloat() else 0f
            for (i in 0 until jumlah) {
                val sudut = i.toFloat() / jumlah * 2f * PI.toFloat() + geser
                titikX[idx] = pusatX + r * cos(sudut)
                titikY[idx] = pusatY + r * sin(sudut)
                titikRing[idx] = k
                idx++
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (titikX.isEmpty()) return

        for (i in titikX.indices) {
            val k = titikRing[i]
            // Gelombang merambat dari tengah ke luar
            val angle = (animProgress * 2 * PI.toFloat()) - k * 0.9f
            val wave = (sin(angle.toDouble()).toFloat() + 1f) / 2f // Rentang 0..1

            // Titik luar lebih besar; ukuran & transparansi bernapas
            val radius = ukuranDasar * (0.55f + 0.45f * k / RING_COUNT) * (0.6f + 0.4f * wave)
            paint.alpha = (90 + 165 * wave).toInt().coerceIn(0, 255)
            canvas.drawCircle(titikX[i], titikY[i], radius, paint)
        }
    }

    private companion object {
        const val RING_COUNT = 5
    }
}

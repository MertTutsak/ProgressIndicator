package com.merttutsak.indicator

import android.content.Context
import android.database.DataSetObserver
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.viewpager.widget.ViewPager
import com.merttutsak.indicator.utils.dp

class ProgressIndicator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val DEFAULT_PROGRESS_TIME = 2
    private val DEFAULT_DOT_SPACING: Float by lazy { 5.dp.toFloat() }
    private val DEFAULT_PROGRESS_COLOR: Int by lazy {
        ContextCompat.getColor(context, R.color.defaultProgressColor)
    }
    private val DEFAULT_IS_INFINITE = false

    private var dotSpacing: Float = DEFAULT_DOT_SPACING
    private var progressTime: Int = DEFAULT_PROGRESS_TIME
    private var progressColor: Int = DEFAULT_PROGRESS_COLOR
    private var isInfinite: Boolean = DEFAULT_IS_INFINITE

    private var linearLayout: LinearLayout? = null

    init {
        LinearLayout(context).also { it.orientation = LinearLayout.HORIZONTAL }.let {
            linearLayout = it
            addView(linearLayout, WRAP_CONTENT, WRAP_CONTENT)
        }

        if (attrs != null) {
            context.obtainStyledAttributes(attrs, R.styleable.Indicator).let {
                progressColor = it.getColor(R.styleable.Indicator_progressColor, DEFAULT_PROGRESS_COLOR)
                dotSpacing = it.getDimension(R.styleable.Indicator_dotSpacing, DEFAULT_DOT_SPACING)
                progressTime = it.getInt(R.styleable.Indicator_progressColor, DEFAULT_PROGRESS_TIME)
                isInfinite = it.getBoolean(R.styleable.Indicator_isInfinite, DEFAULT_IS_INFINITE)
                it.recycle()
            }
        }
    }

    val onPageChangeListener: ViewPager.OnPageChangeListener =
        object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int, positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageScrollStateChanged(state: Int) {
                //nothing
            }

            override fun onPageSelected(position: Int) {
                Log.d("ProgressIndicator", "onPageSelected : $position")
                selectDot(position.rem(dots.size))
            }
        }


    @JvmField
    val dots = arrayListOf<ItemViewModel>()

    var pager: Pager? = null

    //REFRESH
    private fun initDots() {
        if (pager == null) {
            return
        }
        post {
            clear()
            addDots(pager!!.count)
        }
    }

    //ADD
    private fun addDots(count: Int) {
        for (i in 0 until count) {
            addDot(i)
        }
    }

    private fun addDot(index: Int) {
        val root = LayoutInflater.from(context).inflate(R.layout.dot_layout, this, false)

        val item = ItemViewModel(
            progressTime,
            dotSpacing / 2,
            root.findViewById(R.id.rlDot)
        ) {
            //end progress
            next()
        }

        if (index == pager?.currentIndex) {
            item.progress()
        }
        dots.add(item)

        linearLayout?.addView(item.layout)
    }

    //CLEAR
    private fun clear() {
        linearLayout?.removeAllViews()
        dots.clear()
    }

    //RESET
    private fun resetDot() {
        dots.firstOrNull { it.isCurrent() }?.dot()
    }

    private fun resetIndex(index: Int) {
        dots[index].dot()
    }

    //SELECT
    private fun selectDot(index: Int) {
        resetDot()
        dots[index].progress()
    }

    //NEXT
    fun next() {
        if (pager != null && pager!!.count > 1) {
            if (isReturn()) {
                pager!!.setCurrentItem(0)
            } else {
                pager!!.setCurrentItem(pager!!.currentIndex + 1)
            }
        }
    }

    fun setupViewPager(viewPager: ViewPager) {
        if (viewPager.adapter == null) {
            throw IllegalStateException(
                "You have to set an adapter to the view pager before initializing the dots indicator !"
            )
        } else if (viewPager.adapter?.count ?: 0 == Int.MAX_VALUE) {
            throw IllegalStateException(
                "if isInfinite attribute is true, you mustn`t to set as infinite to the viewpager's adapter count. If you want to set the viewpager's adapter count that infinite, you must call the setupInfiniteViewPager function!"
            )
        }

        kotlin.runCatching { viewPager.removeOnPageChangeListener(onPageChangeListener) }
        viewPager.addOnPageChangeListener(onPageChangeListener)

        pager = object : Pager {
            override val currentIndex: Int
                get() = viewPager.currentItem
            override val count: Int
                get() = viewPager.adapter?.count ?: 0

            override fun setCurrentItem(item: Int) {
                viewPager.currentItem = item
            }
        }

        viewPager.adapter?.registerDataSetObserver(object : DataSetObserver() {
            override fun onChanged() {
                super.onChanged()
                initDots()
            }
        })

        initDots()
    }

    fun setupInfiniteViewPager(viewPager: ViewPager, itemCount: Int) {
        if (viewPager.adapter == null) {
            throw IllegalStateException(
                "You have to set an adapter to the view pager before initializing the dots indicator !"
            )
        } else if (isInfinite) {
            throw IllegalStateException(
                "If you called the setupInfiniteViewPager function, You must not define as true the isInfinite attribute!"
            )
        } else if (viewPager.adapter?.count ?: 0 != Int.MAX_VALUE) {
            throw IllegalStateException(
                "If you called setupInfiniteViewPager, The viewpager's adapter count must be infinite!"
            )
        }

        kotlin.runCatching { viewPager.removeOnPageChangeListener(onPageChangeListener) }
        viewPager.addOnPageChangeListener(onPageChangeListener)

        pager = object : Pager {
            override val currentIndex: Int
                get() = viewPager.currentItem
            override val count: Int
                get() {
                    return if (viewPager.adapter?.count ?: 0 != Int.MAX_VALUE) {
                        viewPager.adapter?.count ?: 0
                    } else {
                        itemCount
                    }
                }

            override fun setCurrentItem(item: Int) {
                viewPager.currentItem = item
            }
        }

        viewPager.adapter?.registerDataSetObserver(object : DataSetObserver() {
            override fun onChanged() {
                super.onChanged()
                initDots()
            }
        })

        initDots()
    }

    private fun isReturn(): Boolean {
        return if (pager!!.count > 0) {
            isInfinite && (pager!!.count - 1 == pager!!.currentIndex)
        } else {
            false
        }
    }

    interface Pager {
        val currentIndex: Int
        val count: Int
        fun setCurrentItem(item: Int)
    }
}
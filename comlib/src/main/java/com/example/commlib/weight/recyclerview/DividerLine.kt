package com.example.commlib.weight.recyclerview

import android.R
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.example.commlib.weight.recyclerview.DividerLine

class DividerLine constructor(private val mContext: Context) : ItemDecoration() {
    //divider对应的drawable
    private val dividerDrawable: Drawable?
    private var dividerSize: Int = 0

    //默认为null
    private var mMode: LineDrawMode? = null

    /**
     * 分隔线绘制模式,水平，垂直，两者都绘制
     */
    enum class LineDrawMode {
        HORIZONTAL, VERTICAL, BOTH
    }

    constructor(context: Context, mode: LineDrawMode?) : this(context) {
        mMode = mode
    }

    constructor(context: Context, dividerSize: Int, mode: LineDrawMode?) : this(context, mode) {
        this.dividerSize = dividerSize
    }

    fun getDividerSize(): Int {
        return dividerSize
    }

    fun setDividerSize(dividerSize: Int) {
        this.dividerSize = dividerSize
    }

    fun getMode(): LineDrawMode? {
        return mMode
    }

    fun setMode(mode: LineDrawMode?) {
        mMode = mode
    }

    /**
     * Item绘制完毕之后绘制分隔线
     * 根据不同的模式绘制不同的分隔线
     *
     * @param c
     * @param parent
     * @param state
     */
    public override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        if (getMode() == null) {
            throw IllegalStateException("assign LineDrawMode,please!")
        }
        when (getMode()) {
            LineDrawMode.VERTICAL -> drawVertical(c, parent, state)
            LineDrawMode.HORIZONTAL -> drawHorizontal(c, parent, state)
            LineDrawMode.BOTH -> {
                drawHorizontal(c, parent, state)
                drawVertical(c, parent, state)
            }
        }
    }

    /**
     * 绘制垂直分隔线
     *
     * @param c
     * @param parent
     * @param state
     */
    private fun drawVertical(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val childCount: Int = parent.childCount
        for (i in 0 until childCount) {
            val child: View = parent.getChildAt(i)
            val params: RecyclerView.LayoutParams = child
                    .layoutParams as RecyclerView.LayoutParams
            val top: Int = child.top - params.topMargin
            val bottom: Int = child.bottom + params.bottomMargin
            val left: Int = child.right + params.rightMargin
            val right: Int = if (getDividerSize() == 0) left + dip2px(mContext, DEFAULT_DIVIDER_SIZE.toFloat()) else left + getDividerSize()
            dividerDrawable!!.setBounds(left, top, right, bottom)
            dividerDrawable.draw(c)
        }
    }

    /**
     * 绘制水平分隔线
     *
     * @param c
     * @param parent
     * @param state
     */
    private fun drawHorizontal(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val childCount: Int = parent.childCount
        //        try {
//            //水平绘制的时候查找是否存在RefreshRecyclerView
//            Class viewClass = Class.forName("cn.com.gz01.smartcity.ui.widget.LoadMoreRecyclerView");
//            if (viewClass != null) {
//                if (viewClass == parent.getClass()){
//                    //存在这个类并使用了这个类,就去掉footer的绘制分割线
//                    childCount = childCount - 1;
//                }
//            }
//        } catch (ClassNotFoundException e) {
//            KLog.e(e.getMessage());
//        }
        for (i in 0 until childCount) {
            //分别为每个item绘制分隔线,首先要计算出item的边缘在哪里,给分隔线定位,定界
            val child: View = parent.getChildAt(i)
            //RecyclerView的LayoutManager继承自ViewGroup,支持了margin
            val params: RecyclerView.LayoutParams = child.layoutParams as RecyclerView.LayoutParams
            //child的左边缘(也是分隔线的左边)
            val left: Int = child.left - params.leftMargin
            //child的底边缘(恰好是分隔线的顶边)
            val top: Int = child.bottom + params.topMargin
            //child的右边(也是分隔线的右边)
            val right: Int = child.right - params.rightMargin
            //分隔线的底边所在的位置(那就是分隔线的顶边加上分隔线的高度)
            val bottom: Int = if (getDividerSize() == 0) top + dip2px(mContext, DEFAULT_DIVIDER_SIZE.toFloat()) else top + getDividerSize()
            dividerDrawable!!.setBounds(left, top, right, bottom)
            //画上去
            dividerDrawable.draw(c)
        }
    }

    public override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.bottom = if (getDividerSize() == 0) dip2px(mContext, DEFAULT_DIVIDER_SIZE.toFloat()) else getDividerSize()
        outRect.right = if (getDividerSize() == 0) dip2px(mContext, DEFAULT_DIVIDER_SIZE.toFloat()) else getDividerSize()
    }

    companion object {
        private val TAG: String? = DividerLine::class.java.canonicalName

        //默认分隔线厚度为2dp
        private val DEFAULT_DIVIDER_SIZE: Int = 1

        //控制分隔线的属性,值为一个drawable
        private val ATTRS: IntArray = intArrayOf(R.attr.listDivider)

        /**
         * 将dip或dp值转换为px值，保证尺寸大小不变
         *
         * @param dipValue
         * @param context（DisplayMetrics类中属性density）
         * @return
         */
        fun dip2px(context: Context, dipValue: Float): Int {
            val scale: Float = context.resources.displayMetrics.density
            return (dipValue * scale + 0.5f).toInt()
        }
    }

    init {
        //获取样式中对应的属性值
        val attrArray: TypedArray = mContext.obtainStyledAttributes(ATTRS)
        dividerDrawable = attrArray.getDrawable(0)
        attrArray.recycle()
    }
}
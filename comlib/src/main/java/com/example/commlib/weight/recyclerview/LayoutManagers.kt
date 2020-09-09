package com.example.commlib.weight.recyclerview

import android.content.Context
import androidx.annotation.IntDef
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * A collection of factories to create RecyclerView LayoutManagers so that you can easily set them
 * in your layout.
 */
object LayoutManagers {
    //Animation 默认提供5种方法（渐显、缩放、从下到上，从左到右、从右到左）
    /**
     * 渐显
     */
    val ALPHAIN: Int = BaseQuickAdapter.ALPHAIN

    /**
     * 缩放
     */
    @kotlin.jvm.JvmField
    val SCALEIN: Int = BaseQuickAdapter.SCALEIN

    /**
     * 从下到上
     */
    val SLIDEIN_BOTTOM: Int = BaseQuickAdapter.SLIDEIN_BOTTOM

    /**
     * 从左到右
     */
    val SLIDEIN_LEFT: Int = BaseQuickAdapter.SLIDEIN_LEFT

    /**
     * 从右到左
     */
    val SLIDEIN_RIGHT: Int = BaseQuickAdapter.SLIDEIN_RIGHT

    /**
     * A [LinearLayoutManager].
     */
    fun linear(): LayoutManagerFactory {
        return object : LayoutManagerFactory {
            public override fun create(recyclerView: RecyclerView, context: Context?): RecyclerView.LayoutManager {
                return if (context == null) {
                    LinearLayoutManager(recyclerView.context)
                } else {
                    LinearLayoutManager(context)
                }
            }
        }
    }

    /**
     * A [LinearLayoutManager] with the given orientation and reverseLayout.
     */
    fun linear(@Orientation orientation: Int, reverseLayout: Boolean): LayoutManagerFactory {
        return object : LayoutManagerFactory {
            public override fun create(recyclerView: RecyclerView, context: Context?): RecyclerView.LayoutManager {
                return if (context == null) {
                    LinearLayoutManager(recyclerView.context, orientation, reverseLayout)
                } else {
                    LinearLayoutManager(context, orientation, reverseLayout)
                }
            }
        }
    }

    /**
     * A [GridLayoutManager] with the given spanCount.
     */
    fun grid(spanCount: Int): LayoutManagerFactory {
        return object : LayoutManagerFactory {
            override fun create(recyclerView: RecyclerView, context: Context?): RecyclerView.LayoutManager {
                return if (context == null) {
                    GridLayoutManager(recyclerView.context, spanCount)
                } else {
                    GridLayoutManager(context, spanCount)
                }
            }
        }
    }

    /**
     * A [GridLayoutManager] with the given spanCount, orientation and reverseLayout.
     */
    fun grid(spanCount: Int, @Orientation orientation: Int, reverseLayout: Boolean): LayoutManagerFactory {
        return object : LayoutManagerFactory {
            override fun create(recyclerView: RecyclerView, context: Context?): RecyclerView.LayoutManager {
                return if (context == null) {
                    GridLayoutManager(recyclerView.context, spanCount, orientation, reverseLayout)
                } else {
                    GridLayoutManager(context, spanCount, orientation, reverseLayout)
                }
            }
        }
    }

    /**
     * A [StaggeredGridLayoutManager] with the given spanCount and orientation.
     */
    fun staggeredGrid(spanCount: Int, @Orientation orientation: Int): LayoutManagerFactory {
        return object : LayoutManagerFactory {
            public override fun create(recyclerView: RecyclerView, context: Context?): RecyclerView.LayoutManager {
                return StaggeredGridLayoutManager(spanCount, orientation)
            }
        }
    }

    interface LayoutManagerFactory {
        fun create(recyclerView: RecyclerView, context: Context?): RecyclerView.LayoutManager
    }

    @IntDef(LinearLayoutManager.HORIZONTAL, LinearLayoutManager.VERTICAL)
    @Retention(RetentionPolicy.SOURCE)
    annotation class Orientation constructor()
}
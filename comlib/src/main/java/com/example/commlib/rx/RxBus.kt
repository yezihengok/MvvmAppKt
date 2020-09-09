package com.example.commlib.rx

import com.example.commlib.utils.CommUtils
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by yzh on 19/11/11.
 */
class RxBus {
    private val _bus = PublishSubject.create<Any>().toSerialized()
    fun send(o: Any) {
        _bus.onNext(o)
    }

    fun toObservable(): Observable<Any> {
        return _bus
    }

    /**
     * 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者
     *
     * @param eventType 事件类型
     * @param <T>
     * @return
    </T> */
    fun <T> toObservable(eventType: Class<T>?): Observable<T> {
        return _bus.ofType(eventType)
    }

    /**
     * 提供了一个新的事件,根据code进行分发
     *
     * @param code 事件code
     * @param o
     */
    fun post(code: Int, o: Any?) {
        _bus.onNext(RxBusMessage(code, o))
    }

    /**
     * 根据传递的code和 eventType 类型返回特定类型(eventType)的 被观察者
     * 对于注册了code为0，class为voidMessage的观察者，那么就接收不到code为0之外的voidMessage。
     *
     * @param code      事件code
     * @param eventType 事件类型
     * @param <T>
     * @return
    </T> */
    fun <T> toObservable(code: Int, eventType: Class<T>): Observable<T> {
        CommUtils.isMainThread()
        return _bus.ofType(RxBusMessage::class.java)
                .filter { rxBusMessage -> //过滤code和eventType都相同的事件
                    rxBusMessage.code == code && eventType.isInstance(rxBusMessage.objects)
                }.map { rxBusMessage -> rxBusMessage.objects }.cast(eventType)
    }

    /**
     * 判断是否有订阅者
     */
    fun hasObservers(): Boolean {
        return _bus.hasObservers()
    }

    /**
     * Stciky 相关
     */
    private val mStickyEventMap: MutableMap<Class<*>, Any>

    /**
     * 发送一个新Sticky事件
     */
    fun postSticky(event: Any) {
        synchronized(mStickyEventMap) { mStickyEventMap.put(event.javaClass, event) }
        post(event)
    }

    fun post(event: Any) {
        _bus.onNext(event)
    }

    /**
     * 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者
     */
    fun <T> toObservableSticky(eventType: Class<T>): Observable<T> {
        synchronized(mStickyEventMap) {
            val observable = _bus.ofType(eventType)
            val event = mStickyEventMap[eventType]
            return if (event != null) {
                observable.mergeWith(Observable.create {
                        emitter: ObservableEmitter<T?> ->
                    eventType.cast(event)?.let { emitter.onNext(it) }
                })
            } else {
                observable
            }
        }
    }

    /**
     * 根据eventType获取Sticky事件
     */
    fun <T> getStickyEvent(eventType: Class<T>): T? {
        synchronized(mStickyEventMap) { return eventType.cast(mStickyEventMap[eventType]) }
    }

    /**
     * 移除指定eventType的Sticky事件
     */
    fun <T> removeStickyEvent(eventType: Class<T>): T? {
        synchronized(mStickyEventMap) { return eventType.cast(mStickyEventMap.remove(eventType)) }
    }
    //    在使用Sticky特性时，在不需要某Sticky事件时， 通过removeStickyEvent(Class<T> eventType)移除它，最保险的做法是：在主Activity的onDestroy里removeAllStickyEvents()。
    //    因为我们的RxBus是个单例静态对象，再正常退出app时，该对象依然会存在于JVM，除非进程被杀死，这样的话导致StickyMap里的数据依然存在，为了避免该问题，需要在app退出时，清理StickyMap。
    /**
     * 移除所有的Sticky事件
     */
    fun removeAllStickyEvents() {
        synchronized(mStickyEventMap) { mStickyEventMap.clear() }
    }

    companion object {
        /**
         * 参考:
         * http://www.loongwind.com/archives/264.html
         * https://blog.csdn.net/u013651026/article/details/79088442
         */
        //RxBus=用RxJava模拟实现的EventBus的功能,不需要再额外引入EventBus库增加app代码量
        //        RxBus.instance.post(1,"呵呵呵哒");
        //
        //        RxBus.instance.toObservable(String.class).subscribe(new Consumer<String>() {
        //            @Override
        //            public void accept(String s) throws Exception {
        //
        //            }
        //        });
        //    }
        //        RxBus.instance.postSticky(new EventSticky("aa"));
        //        RxBus.instance.toObservableSticky(EventSticky.class).subscribe(new Consumer<EventSticky>() {
        //        @Override
        //        public void accept(EventSticky eventSticky) throws Exception {
        //
        //        }
        //    });
        //    public class EventSticky {
        //        public String event;
        //
        //        public EventSticky(String event) {
        //            this.event = event;
        //        }
        //
        //        @Override
        //        public String toString() {
        //            return "EventSticky{" +
        //                    "event='" + event + '\'' +
        //                    '}';
        //        }
        //    }
        @JvmStatic
        val instance: RxBus by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { RxBus() }
    }

    init {
        mStickyEventMap = ConcurrentHashMap()
    }
}
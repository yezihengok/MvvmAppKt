package com.example.mykotlintest.bean

import test_kt.bean.Listener

/**
 * 与Java一样一个类或者对象可以实现一个或多个接口。
 */

//接口中的属性只能是抽象的，不允许初始化值，接口不会保存属性值，实现接口时，必须重写属性：
class Child : Listener {
    override var name: String = "aaaaa" //重写属性

    override fun isOK() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
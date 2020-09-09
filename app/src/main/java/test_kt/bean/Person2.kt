package com.example.mykotlintest.bean

/**
 *
    在基类中，使用fun声明函数时，此函数默认为final修饰，不能被子类重写。如果允许子类重写该函数，
    那么就要手动添加 open 修饰它, 子类重写方法使用 override 关键词：
 */

open class Person2 {
    open fun study(){       // 允许子类重写
        println("我学习了")
    }



}


/**子类继承 Person 类**/
class Student2 : Person2() {

    override fun study(){    // 重写方法
        println("我在读大学")
    }
}




//如果有多个相同的方法（继承或者实现自其他类，如A、B类），则必须要重写该方法，使用super范型去选择性地调用父类的实现。


open class A {
    open fun test () { print("A-test") }
    fun a() { print("a") }
}

interface B {
    fun aa() { print("B") } //接口的成员变量默认是 open 的
    fun test() { print("B-test") }
}

class C() : A() , B{
    override fun test() {
        super<A>.test()//调用 A.f()
        super<B>.test()//调用 B.f()
    }
}




/***属性重写**/
open class Foo {
    open val x=3
}

class Bar1 : Foo() {
    override val x: Int =  5
}
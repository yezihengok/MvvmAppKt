package com.example.mykotlintest.bean

/**
 *如果一个类要被继承，可以使用 open 关键字进行修饰。
 */

open class Base(p: Int)

//Kotlin 中所有类都继承该 Any 类，它是所有类的超类， 注意：Any 不是 java.lang.Object。
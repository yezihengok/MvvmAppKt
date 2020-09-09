package com.example.mykotlintest

import android.widget.MultiAutoCompleteTextView

/**
 *
 * @Author: yzh
 * @CreateDate: 2019/9/25 10:40
 */

//定义泛型类型变量，可以完整地写明类型参数，如果编译器可以自动推定类型参数，也可以省略类型参数。
//
//Kotlin 泛型函数的声明与 Java 相同，类型参数要放在函数名的前面：
class Box<T>(t : T) {
    var value = t
}

fun main(args: Array<String>) {
    var boxInt = Box<Int>(10)
    var boxString = Box<String>("test")

    var boxString2 = Box("test2") // 编译器会进行类型推断<String>可以省略

    println(boxInt.value)
    println(boxString.value)


    /**泛型约束**/
    //我们可以使用泛型约束来设定一个给定参数允许使用的类型。
    //Kotlin 中使用 : 对泛型的类型上限进行约束。
    sort(listOf(1, 2, 3)) // OK。Int 是 Comparable<Int> 的子类型
    //sort(listOf(HashMap<Int, String>())) // 错误：HashMap<Int, String> 不是 Comparable<HashMap<Int, String>> 的子类型

}

fun <T : Comparable<T>> sort(list: List<T>) {
    // ……
}
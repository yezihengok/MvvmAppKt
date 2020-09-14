package com.example.mykotlintest

/**
 *
 * @Author: yzh
 * @CreateDate: 2019/9/5 16:16
 */

fun main() {
    println("hello,kotlin")

    /**lambda表达式是返回函数体内最后一行表达式的值 **/

//    () -> Unit//表示无参数无返回值的Lambda表达式类型
//
//    (T) -> Unit//表示接收一个T类型参数，无返回值的Lambda表达式类型
//
//    (T) -> R//表示接收一个T类型参数，返回一个R类型值的Lambda表达式类型
//
//    (T, P) -> R//表示接收一个T类型和P类型的参数，返回一个R类型值的Lambda表达式类型
//
//    (T, (P,Q) -> S) -> R//表示接收一个T类型参数和一个接收P、Q类型两个参数并返回一个S类型的值的Lambda表达式类型参数，返回一个R类型值的Lambda表达式类型


/*    完整表达方式：
    val 函数名 : (参数1类型, 参数2类型, ...) -> 返回值类型 = { 参数1, 参数2, ... -> 函数体 }

    表达式返回值类型可自动推断形式
    val 函数名 = { 参数1:类型1, 参数2:类型2, ... -> 函数体 }*/

    val sum1: (Int, Int) -> Int = { a, b -> a + b }
    // 等价于
    val sum2 = { a: Int, b: Int -> a + b }

    // 等价于函数
    fun sum(a: Int, b: Int): Int {
        return a + b
    }



/*    匿名函数
    匿名函数形式为：

    val 函数名 = fun(参数1:类型1, 参数2:类型2, ...): 返回值类型 { 函数体 }
    示例：*/

    val sum3 = fun(a: Int, b: Int): Int {
        return a + b
    }

    // 等价于函数
    fun sum4(a: Int, b: Int): Int {
        return a + b
    }
}

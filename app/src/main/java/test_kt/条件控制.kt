package com.example.mykotlintest

/**
 *
 * @Author: yzh
 * @CreateDate: 2019/9/23 14:54
 */

fun main(){

    /**IF 表达式**/
    var a= 1
    var b=2

    // 传统用法
    var max=a
    if (a < b) max = b

// 使用 else
    var max2: Int
    if (a > b) {
        max2 = a
    } else {
        max2 = b
    }

    // 作为表达式
    val max3 = if (a > b) a else b

//可以把 IF 表达式的结果赋值给一个变量。

    val max4 = if (a > b) {
        print("Choose a")
        a
    } else {
        print("Choose b")
        b
    }


    //可以不用三元操作符实现
//    val c = if (condition) a else b

    /**使用区间--使用 in 运算符来检测某个数字是否在指定区间内**/
    val x = 7
    if (x in 1..8) {
        println("x 在区间内")
    }


    /**When 表达式**/
    //when 类似其他语言的 switch 操作符。 else 等同 switch 的 default
    when (x) {
        1 -> print("x == 1")
        2 -> print("x == 2")
        3, 4 -> print("x == 4 or x == 4")//如果很多分支需要用相同的方式处理，则可以把多个分支条件放在一起，用逗号分隔

//可以检测一个值在（in）或者不在（!in）一个区间或者集合中：
        in 1..5 -> print("x is in the range")
//        !in 10..20 -> print("x is outside the range")

        else -> { // 上面条件都不满足执行
            print("x 不是 1 ，也不是 2")
        }
    }


    ///（is）或者不是（!is）一个特定类型的值
    fun hasPrefix(x: Any) = when(x) {
        is String -> x.startsWith("prefix")
        else -> false
    }

    //when 中使用 in 运算符来判断集合内是否包含某实例：
    val items = setOf("apple", "banana", "kiwi")
    when {
        "orange" in items -> println("juicy")
        "apple" in items -> println("apple is fine too")
    }




    /**for 循环**/

    //正常循环：

    for (i in 1..4) print(i) // 打印结果为: "1234"
    // 如果你需要按反序遍历整数可以使用标准库中的 downTo() 函数:

    for (i in 4 downTo 1) print(i) // 打印结果为: "4321"也支持指定步长：
    for (i in 1..4 step 2) print(i) // 打印结果为: "13"

    for (i in 4 downTo 1 step 2) print(i) // 打印结果为: "42"
    //如果循环中不要最后一个范围区间的值可以使用 until 函数:

    for (i in 1 until 10) { // i in [1, 10), 不包含 10
        println(i)
    }

    val collection = listOf("呵呵", "嘿嘿", "哈哈")
    for (item in collection) print(item)

    //根据索引 i 循环
    for (i in collection.indices) {
        print(collection[i])
    }

    /**返回和跳转--与Javay基本一样**/

//    return。默认从最直接包围它的函数或者匿名函数返回。
//    break。终止最直接包围它的循环。
//    continue。继续下一次最直接包围它的循环。

    /**Break 和 Continue 标签**/
    //在 Kotlin 中任何表达式都可以用标签（label）来标记。 标签的格式为标识符后跟 @ 符号，例如：abc@、fooBar@都是有效的标签

    loop@ for (i in 1..100) {
        for (j in 1..100) {
            if (i==51) break@loop
        }
    }

}

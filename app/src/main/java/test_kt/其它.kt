package test_kt

import test_kt.bean.User

/**
 *
 *  高阶函数---将函数用作参数或返回值的函数。
 *
 * Created by yzh on 2020/9/14 11:28.
 */
data class Test(val name: String, val age: Int)

fun main() {




    val test=listOf(2,4,6,8,15)
    //todo  filter函数遍历集合并选出应用给定lambda后会返回true的那些元素
    println("大于5的数 ${test.filter { it > 5 }}")

    test.filterNot { it > 5 }.forEach {s-> println(" 不满足条件>5的==$s") }

    println("---------------------------------------------")
    //todo map函数对集合中的每一个元素应用给定的函数并把结果收集到一个新集合
    println("平方操作 ${test.map { it * it }}")

    val testList = listOf(Test("aa", 3), Test("bb", 12), Test("cc", 10), Test("dd", 2))
    // 将一个列表转换为另一个列表
    println("只展示name ${testList.map { it.name }}")

    // filter与map链式操作
    println("展示age大于10的name ${testList.filter { it.age > 10 }.map { it.name }}")

    println("---------------------------------------------")
    //todo all & any & count & find

    // all判断是否全部符合lambda表达式的条件
    println("是否全部符合>10 ${test.all { it > 10 }}")
    // any判断是否存在有符合lambda表达式的条件的数据
    println("是否存在>8 ${test.any { it > 8 }}")
    // count获取符合lambda表达式条件的数据个数
    println("大于5的个数 ${test.count { it > 5 }}")
    // find获取符合lambda表达式条件的第一个数据
    println("第一个大于5 ${test.find { it > 5 }}")
    println("最后一个大于5 ${test.findLast { it > 5 }}")
    println("---------------------------------------------")

    //flatMap()代表了一个一对多的关系，可以将每个元素变换为一个新的集合，再将其平铺成一个集合。
    //groupBy()方法会返回一个Map<k,list>的Map对象，其中Key就是我们分组的条件，value就是分组后的集合。<k,list>
    val test2 = listOf("a", "ab", "b", "bc")

    // groupBy按照lambda表达式的条件重组数据并分组
    println("按首字母分组 ${test2.groupBy(String::first)}")

    // partition按照条件进行分组，该条件只支持Boolean类型条件，first为满足条件的，second为不满足的
    test2.partition { it.length > 1 }.first.forEach { print("$it、") }
    println()
    test2.partition { it.length > 1 }.second.forEach { print("$it、") }
    println()

    // flatMap首先按照lambda表达式对元素进行变换，再将变换后的列表合并成一个新列表
    println(test2.flatMap { it.toList()})// it.toList() =把每个字符串拆分成一个个char


    // reduce函数将一个集合的所有元素通过传入的操作函数实现数据集合的累积操作效果。
    println(test2.reduce { acc, name -> "$acc--$name" })
    println(test2.reduce { acc, name -> acc+name })

    println("---------------------------------------------")

    val test3 = listOf(3, 2, 4, 6, 7, 1)
    //sortedBy()用于根据指定的规则进行顺序排序，如果要降序排序，则需要使用sortedByDescending()
    println(test3.sortedBy { it })
    println("---------------------------------------------")

    //todo take()和slice()用于进行数据切片，从某个集合中返回指定条件的新集合。类似的还有takeLast()、takeIf()

    // 获取前3个元素的新切片
    println(test3.take(3))

    // 获取指定index组成的新切片
    println(test3.slice(IntRange(2, 4)))

//takeIf：会根据入参的函数的返回值（true/false），决定自己（takeIf）的返回值是null还是调用者。如果是false，那么就会返回null，因此这里使用?的方式继续去调用后续操作。
    function(User("cc", 10))

}

fun function(user : User?){
    //传的不为空，且年龄>10 则 打印他的姓名:
    user?.takeIf{
        it.age > 10
    }?.let{
        print(it.name)
    }
}
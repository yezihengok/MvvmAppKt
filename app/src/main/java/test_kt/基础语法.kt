package com.example.mykotlintest



//kotlin的入口函数-入口函数必须定义在包级 , class外面
fun main() {
    println("hello,kotlin")

    //    可变变量定义：var 关键字
//    不可变变量定义：val 关键字，只能赋值一次的变量(类似Java中final修饰的变量)

    val a: Int = 1
    val b = 1       // 系统自动推断变量类型为Int
    val c: Int      // 如果不在声明时初始化则必须提供变量类型
    c = 1           // 明确赋值


    var x = 5        // 系统自动推断变量类型为Int
    x += 1           // 变量可修改

    /**函数定义 函数定义使用关键字 fun，参数格式为：参数 : 类型*/
    println("结果为:"+sum(2,3))
    varss(1,2,3)


    /**字符串模板*/
    //$varName 表示变量值
    //${varName.fun()} 表示变量的方法返回值:
    var aa=2
    val s="a的结果是$aa"
    val s1="原内容【$s】,现内容【${s.replace("的","の")}】"
    println(s1)


    /**NULL检查机制*/
    ////////类型后面加?表示可为空
    var age: String? = "23"

    //如果age为null，抛出空指针异常
    val ages = age!!.toInt()
    //如果age为null，不做处理返回 null
    val ages1 = age?.toInt()
    //如果age为null，age为空返回-1
    val ages2 = age?.toInt() ?: -1

    // parseInt("")

    /**区间*/
    for (i in 1..3) print(i)  // 输出“123”
    for (i in 4..1) print(i) // 什么都不输出,应使用 for (i in 4 downTo 1 )

    val i=2
    if (i in 1..10) { // 等同于 1 <= i && i <= 10
        println(i)
    }

// 使用 step 指定步长
    for (i in 1..4 step 2) print(i) // 输出“13”
    for (i in 4 downTo 1 step 2) print(i) // 输出“42”

// 使用 until 函数排除结束元素
    for (i in 1 until 10) {   // i in [1, 10) 排除了 10
        println(i)
    }


}



//当一个引用可能为 null 值时, 对应的类型声明必须明确地标记为可为 null。
//当 下面str 中的字符串内容不是一个整数时, 返回 null:
fun parseInt(str: String): Int? {
    return null
}

/**类型检测及自动类型转换**/
fun getStringLength(obj: Any): Int? {
    if (obj !is String)
        return null
    // 在这个分支中, `obj` 的类型会被自动转换为 `String`
    return obj.length
}


fun sum(a:Int,b: Int):Int{    // Int 参数，返回值 Int
    return a+b
}

//表达式作为函数体，返回类型自动推断：
fun sum1(a: Int,b: Int)=a+b

//无返回值的函数(类似Java中的void)：
fun sum2(a: Int,b: Int):Unit{
    print(a+b)
}

//可变长参数函数用 vararg 关键字进行标识 等同于Java int...
fun varss(vararg v:Int){
    for(vt in v){
        print(vt)
    }
}

package com.example.mykotlintest.bean

/**
 *
 * @Author: yzh
 * @CreateDate: 2019/9/23 15:53
 */


// 主构造器
class User2(firstName: String) {//住构造函数constructor关键字可以省略
    var name: String=""
    var age: Int=0
    var city: String=""

    //主构造器中不能包含任何代码，初始化代码可以放在初始化代码段中，初始化代码段使用 init 关键字作为前缀。
    init {
        println("初始化FirstName is $firstName")
    }



    class Nested {// 嵌套类
        fun foo() = 2
    }

    //内部类使用 inner 关键字来表示。

    var v = "成员属性"
    /**嵌套内部类**/
    inner class Inner {
        fun foo() = city  // 访问外部类成员
        fun innerTest() {
            var o = this@User2 //获取外部类的成员变量
            println("内部类可以引用外部类的成员，例如：" + o.v)
        }
    }

}
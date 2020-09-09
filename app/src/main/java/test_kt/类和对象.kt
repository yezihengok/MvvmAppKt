package com.example.mykotlintest

import com.example.mykotlintest.bean.*

/**
 *
 * @Author: yzh
 * @CreateDate: 2019/9/23 15:50
 */
fun main(){
    val site = User() // Kotlin 中没有 new 关键字
    val sites = User2("AA")

    //调用嵌套类方法
    var a= User2.Nested().foo()

    //调用内部类方法
    val demo2 = User2("BB").Inner().innerTest()

    /***
     * 类的修饰符
     */

/*    abstract    // 抽象类
    final       // 类不可继承，默认属性
    enum        // 枚举类
    open        // 类可继承，类默认是final的
    annotation  // 注解类*/

/*    private    // 仅在同一个文件中可见
    protected  // 同一个文件中或子类可见
    public     // 所有调用的地方都可见
    internal   // 同一个模块中可见*/



    //如果子类没有主构造函数，则必须在每一个二级构造函数中用 super 关键字初始化基类，或者在代理另一个构造函数。初始化基类时，可以调用基类的不同构造方法。
    /**继承**/
    var s =  Student("SYMS", 18, "AKB48", 89)

    /**重写**/
    val s2 =  Student2()
    s2.study()

    val c =  C()
    c.test()

    /**接口**/
    val d =  Child()
    d.foo()
    d.isOK()

}

/**主构造方法**/
//主构造方法：
class InnerClass constructor(name: String) {
    var myName = name
}

//constructor 可以省略
class InnerClasss(name: String){
    var myName = name

    //次构造方法
    constructor(name: String, age: Int) : this(name) {

    }

    var inner = InnerClass("呵呵") //Kotlin中是没有new的

}

/**类的继承**/
//kotlin的继承使用的是“：”冒号
open class Person{
    //person这个类前面有一个open关键词，open在kotlin中代表了，这个类可以被继承，如果不写，其他类是无法继承该类的
  var  aaa="1"
}

class Man : Person(){
    val b=aaa
}


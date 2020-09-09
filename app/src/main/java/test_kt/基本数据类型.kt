package com.example.mykotlintest

/**
 *字面常量
 * 十进制：123
    长整型以大写的 L 结尾：123L
    16 进制以 0x 开头：0x0F
    2 进制以 0b 开头：0b00001011
    注意：8进制不支持

 */


fun test(){
    //可以使用下划线使数字常量更易读：
    val oneMillion = 1_000_000
    val creditCardNumber = 1234_5678_9012_3456L
    val socialSecurityNumber = 999_99_9999L
    val hexBytes = 0xFF_EC_DE_5E
    val bytes = 0b11010010_01101001_10010100_10010010
}
 fun main(){
    /**比较两个数字*/
//     Kotlin 中没有基础数据类型，只有封装的数字类型 三个等号 === 表示比较对象地址，两个 == 表示比较两个值大小。
     val a: Int = 10000
     println(a === a) // true，值相等，对象地址相等

     //经过了装箱，创建了两个不同的对象
     val boxedA: Int? = a
     val anotherBoxedA: Int? = a

     //虽然经过了装箱，但是值是相等的，都是10000
     println(boxedA === anotherBoxedA) //  false，值相等，对象地址不一样
     println(boxedA == anotherBoxedA) // true，值相等

    /**类型转换*/
    //较小类型并不是较大类型的子类型，较小的类型不能隐式转换为较大的类型
     //如下：不进行显式转换的情况下我们不能把 Byte 型值赋给一个 Int 变量

    val bb: Byte = 1 // OK, 字面值是静态检测的
//     val i2: Int = bb // 错误error

//     我们可以代用其toInt()方法。
     val bb2: Byte = 1 // OK, 字面值是静态检测的
     val ii: Int = bb2.toInt() // OK


     //有些情况下也可以使用自动类型转化，前提是可以根据上下文环境推断出正确的数据类型而且数学操作符会做相应的重载:
    var v=1L+3 // Long + Int ==> Long


     /**位操作符*/
/*     对于Int和Long类型，还有一系列的位操作符可以使用，分别是：
     shl(bits) – 左移位 (Java’s <<)
     shr(bits) – 右移位 (Java’s >>)
     ushr(bits) – 无符号右移位 (Java’s >>>)
     and(bits) – 与
     or(bits) – 或
     xor(bits) – 异或
     inv() – 反向*/




     /**数组*/
//     数组的创建两种方式：一种是使用函数arrayOf()；另外一种是使用工厂函数:
     //[1,2,3]
     val a1 = arrayOf(1, 2, 3)
     //[0,2,4]
     val b = Array(3, { i -> (i * 2) })

     println(a1[0])    // 输出结果：1
     println(b[1])    // 输出结果：2
     //除了类Array，还有ByteArray, ShortArray, IntArray 用法一样



     /**字符**/
//     和 Java 不一样，Kotlin 中的 Char 不能直接和数字操作，Char 必需是单引号 ' 包含起来的。比如普通字符 '0'，'a'。
     fun check(c: Char) {
         //  if (c == 1) { // 错误：类型不兼容
         if (c == '1') {
         }
     }

     //可以显式把字符转换为 Int 数字：
     fun decimalDigitValue(c: Char): Int {
         if (c !in '0'..'9')
             throw IllegalArgumentException("Out of range")
         return c.toInt() - '0'.toInt() // 显式转换为数字
     }


     /**字符串*/
     //和 Java 一样，String 是不可变的。方括号 [] 语法可以很方便的获取字符串中的某个字符，也可以通过 for 循环来遍历：
     var str="呵呵哒"
     for (c in str) {
         print(str[2])
         println(c)
     }

//     Kotlin 支持三个引号 """ 扩起来的字符串，支持多行字符串，比如：
     val text = """
     多行字符串1
     多行字符串2
     多行字符串3
    >>多行字符串N
     """
     .trimMargin(">>")//String 可以通过 trimMargin() 方法来删除多余的空白。
     println(text)   // 输出有一些前置空格
     println("--------------------------------------------")

     /**字符串模板$*/
     var str1=3
     println("今天喝了$str1 次水")
     println("今天吃了${str1+1}顿饭")

     var amount=9.98
     var price="价格：$amount"  //要打印字符串 "价格：$3" 怎么办:

     var prices="价格：${'$'}$amount"

     println(price)
     println(prices)
 }
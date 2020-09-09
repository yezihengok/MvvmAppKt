package test_kt



//TODO======Kotlin关于可以为空的判断======
/*1，
length_null = strB?.length

?.表示对象为空时就直接返回null，如果对象不为空，则返回strB.length。所以返回值的变量必须被声明为可空类型

length = strB?.length?: -1

?:表示为空时就返回右边的值，即(x!=null)? x.** : y

ength = strB!!.length

运算符“!!”强行放弃了非空判断，开发者就得自己注意排雷了。否则的话，一旦出现空指针，App运行时依然会抛出异常*/

/*1、声明对象实例时? ，在类型名称后面加问号，表示该对象可以为空；
  2、调用对象方法时?. ，在实例名称后面加问号，表示一旦实例为空就返回null；
  3、新引入运算符“?:”，一旦实例为空就返回该运算符右边的表达式；
  4、新引入运算符“!!”，通知编译器不做非空校验，运行时一旦发现实例为空就扔出异常；*/

/**
 * Created by yzh on 2020/8/18 13:38.
 */
class Site(val map: Map<String, Any?>) {
    val name: String by map
    val url: String  by map
}

fun main(args: Array<String>) {
    // 构造函数接受一个映射参数
    val site = Site(mapOf(
        "name" to "菜鸟教程",
        "url"  to "www.runoob.com"
    ))

    // 读取映射值
    println(site.name)
    println(site.url)



}
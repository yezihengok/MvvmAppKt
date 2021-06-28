package test_kt

/**
 * Created by yzh on 2020/9/19 10:09.
 */
class TestBean {
    var name: String = "xuyisheng"
    var age: Int = 18
}


    /*run - let
   返回值为函数最后一行或者return指定的表达式;  run函数体内使用this代替本对象。let 则是 it


    apply - also
    返回值为本对象。 apply 函数内使用this代替本对象。  also则是 it


    takeIf
    条件为真返回对象本身否则返回null。

    takeUnless
    条件为真返回null否则返回对象本身。

    with
    with比较特殊，不是以扩展方法的形式存在的，而是一个顶级函数。
    传入参数为对象，函数内使用this代替对象。
    返回值为函数最后一行或者return指定的表达式。

    repeat
    将函数体执行多次。*/
fun main(args: Array<String>) {
    val test = TestBean()
    val resultRun = test.run {
        name = "xys"
        age = 3
        println("Run内部 $this")
        age
    }
    println("run返回值 $resultRun")
    val resultLet = test.let {
        it.name = "xys"
        it.age = 3
        println("let内部 $it")
        it.age
    }
    println("let返回值 $resultLet")
    val resultApply = test.apply {
        name = "xys"
        age = 3
        println("apply内部 $this")
        age
    }
    println("apply返回值 $resultApply")

    val resultAlso = test.also {
        it.name = "xys"
        it.age = 3
        println("also内部 $it")
        it.age
    }
    println("also返回值 $resultAlso")
    val resultWith = with(test) {
        name = "xys"
        age = 3
        println("with内部 $this")
        age
    }
    println("with返回值 $resultWith")
    test.age = 33

    //takeIf：会根据入参的函数的返回值（true/false），决定自己（takeIf）的返回值是null还是调用者。如果是false，
    // 那么就会返回null，因此这里使用?的方式继续去调用后续操作。
    val resultTakeIf = test.takeIf {
        it.age > 3
    }

    println("takeIf $resultTakeIf")
    val resultTakeUnless = test.takeUnless {
        it.age > 3
    }
    println("takeUnless $resultTakeUnless")
}

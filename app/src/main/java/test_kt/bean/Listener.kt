package test_kt.bean

/**
 *接口中的属性只能是抽象的，不允许初始化值，接口不会保存属性值，实现接口时，必须重写属性：
 */
interface Listener {
    var name:String //name 属性, 抽象的

    fun isOK()    // 未实现
    fun foo() {  //已实现
        // 可选的方法体
        println("foo")
    }
}


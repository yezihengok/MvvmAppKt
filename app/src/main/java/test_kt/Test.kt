package test_kt

import com.blankj.ALog
import kotlinx.coroutines.*


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



//todo Kotlin 协程
      var mainScope = MainScope()
/*    一般程序会有一个主进程，主进程中可能含有多个线程。而协程，是线程中的，也就是说一个线程中可能包含多个协程，协程与协程之间是可以嵌套的。
       简言：在Kotlin里协程就是由官方提供的一套线程API*/

//    协程就像非常轻量级的线程。线程是由系统调度的，线程切换或线程阻塞的开销都比较大。而协程依赖于线程，
//    但是协程挂起时不需要阻塞线程，几乎是无代价的，协程是由开发者控制的。所以协程也像用户态的线程，非常轻量级，
//    一个线程中可以创建任意个协程。协程很重要的一点就是当它挂起的时候，它不会阻塞其他线程。


    // 协程依赖
    //implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.1.1"

    //切换到后台执行IO操作
    GlobalScope.launch(Dispatchers.IO) {
       // saveToDisk(data)
    }
    //切換到主线程
    GlobalScope.launch(Dispatchers.Main) {  // ** launch 函数里一连串的代码片段统称为协程**
        //undateUi(data)
    }
   // 如果只用launch函数，那么协程并不能做太多的事情，仅仅是切换或者指定线程，所以还要配合withContext（）

   // withContext（）函数可以指定线程来执行代码，并且执行完毕后再切换回来，继续执行原先线程里的后续代码
    GlobalScope.launch {
        var img= withContext(Dispatchers.IO){
            //saveImageToDisk(data)
        }
        //userImage.setImageBitmap(img)
    }
    //上面代码，可以解释成，在主线程里，启用了协程，并且指定在IO线程里操作，当操作完成后，会自动切换回主线程，进行页面的刷新操作



    //有自动切换的操作，所以在处理并发任务的时候，可以把WithContext单独抽取出来，放到函数里，包裹着实际操作的耗时代码，例如：

//    但是如果直接fun这样写，是会报错的，因为withContext方法需要在协程里面被调用，所以saveImageToDisk函数前面要加上suspend 关键字，提醒调用者，我是一个耗时操作，我要在协程里被调用。
//    suspend是kotlin协程中一个非常重要的关键字，当执行有被suspend关键字修饰的函数式，线程会被挂起，并且这个挂起是非阻塞式的，不会影响后面的继续执行。

   // 挂起的是协程，也就是launch包裹的代码片段，也就是说，这个线程跟这个协程从此脱离了，不是线程暂停了。
    suspend fun saveImageToDisk(data:String) {
        withContext(Dispatchers.IO) {
            // save()
        }
    }

    GlobalScope.launch {
        var image= saveImageToDisk("data")
       // userImage.setImageBitmap(image)
    }

/*    Dispatchers.Main：Android中的主线程，可以直接操作UI
    Dispatchers.IO：针对磁盘和网络IO进行了优化，适合IO密集型的任务，比如：读写文件，操作数据库以及网络请求
    Dispatchers.Default：适合CPU密集型的任务，比如解析JSON文件，排序一个较大的list
    注意BaseActivity要继承 CoroutineScope by MainScope()，launch才能被识别*/

//    所谓的挂起，其实本质上还是切换了一个线程
//    到这里，总结下
//    协程，本质上就是一个线程切换框架
//    挂起，本质上是就是切换线程，
//    协程的非阻塞式，和java里线程的非阻塞是一样，只不过更方便。








}
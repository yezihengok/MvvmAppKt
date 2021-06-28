package test_kt

import java.util.*

/**
 * Created by yzh on 2020/11/6 9:36.
 */
class Testtt {

}


fun main(){
    val line = "你好吗 ?不过如此呢!没问 题好的好的，明天见。ok呢"
    val after = line.split("[?!,.？！，。]".toRegex()).toTypedArray()
    after?.let {
        for (i in it.indices) {
            println(after[i].trim { it <= ' ' })
        }
    }



//    val stringList: List<String> = ArrayList()
//    stringList.a
//    stringList.removeAt(0)


//    val a1 = arrayOf(1, "好的", 3)
//    for (i  in a1){
//        println(i)
//    }

}

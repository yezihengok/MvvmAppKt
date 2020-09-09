package com.example.commlib.rx

class RxBusMessage {
    var code = 0
        private set
    var objects: Any? = null
        private set

    constructor(code: Int, objects: Any?) {
        this.code = code
        this.objects = objects
    }

    constructor() {}

}
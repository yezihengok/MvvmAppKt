package com.example.mvvmapp.bean

/**
 * @author jingbin
 * @data 2018/5/7
 * @Description
 */
class LoginBean {
    /**
     * data : {"collectIds":[2317,2255,2324],"email":"","icon":"","id":1534,"password":"jingbin54770","type":0,"username":"jingbin"}
     * errorCode : 0
     * errorMsg :
     */
    //    private User data;
    //    private int errorCode;
    //    private String errorMsg;
    //    public User getData() {
    //        return data;
    //    }
    //
    //    public void setData(User data) {
    //        this.data = data;
    //    }
    //
    //    public int getErrorCode() {
    //        return errorCode;
    //    }
    //
    //    public void setErrorCode(int errorCode) {
    //        this.errorCode = errorCode;
    //    }
    //
    //    public String getErrorMsg() {
    //        return errorMsg;
    //    }
    //
    //    public void setErrorMsg(String errorMsg) {
    //        this.errorMsg = errorMsg;
    //    }
    class DataBean {
        /**
         * collectIds : [2317,2255,2324]
         * email :
         * icon :
         * id : 1534
         * password : jingbin54770
         * type : 0
         * username : jingbin
         */
        var email: String? = null
        var icon: String? = null
        var id = 0
        var password: String? = null
        var type = 0
        var username: String? = null
        var collectIds: List<Int>? = null
    }
}
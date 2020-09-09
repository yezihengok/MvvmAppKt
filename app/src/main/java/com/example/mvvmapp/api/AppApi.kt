package com.example.mvvmapp.api

import com.example.commlib.bean.ResultBean
import com.example.commlib.bean.ResultBeans
import com.example.mvvmapp.bean.HomeListBean
import com.example.mvvmapp.bean.LoginBean
import com.example.mvvmapp.bean.WanAndroidBannerBean
import io.reactivex.Observable
import retrofit2.http.*

/**
 * @Description: AppApi类作用描述
 * @Author: yzh
 * @CreateDate: 2019/11/16 14:17
 */
interface AppApi {
    /**
     * 玩安卓轮播图
     */
    @get:GET("banner/json")
    val wanBanner: Observable<ResultBeans<WanAndroidBannerBean>>

    /**
     * 玩安卓，文章列表、知识体系下的文章
     *
     * @param page 页码，从0开始
     * @param cid  体系id
     */
    @GET("article/list/{page}/json")
    fun getHomeList(@Path("page") page: Int, @Query("cid") cid: Int?): Observable<ResultBean<HomeListBean>>

    @GET("article/list/{page}/json")
    fun getHomeList(@Path("page") page: Int, @QueryMap map: Map<String, Int>?): Observable<ResultBean<HomeListBean>>

    /**
     * 玩安卓登录
     *
     * @param username 用户名
     * @param password 密码
     */
    @FormUrlEncoded
    @POST("user/login")
    fun login(@Field("username") username: String?, @Field("password") password: String?): Observable<LoginBean>

    /**
     * 玩安卓注册
     */
    @FormUrlEncoded
    @POST("user/register")
    fun register(@Field("username") username: String?, @Field("password") password: String?, @Field("repassword") repassword: String?): Observable<LoginBean>
}
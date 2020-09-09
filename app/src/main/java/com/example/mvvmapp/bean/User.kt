package com.example.mvvmapp.bean

class User(
    var id: Int,
    var email: String,
    var icon: String,
    var password: String,
    var type: Int,
    var username: String,
    var coinCount: Int,
    var rank: Int
) {
    override fun toString(): String {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", icon='" + icon + '\'' +
                ", password='" + password + '\'' +
                ", type=" + type +
                ", username='" + username + '\'' +
                ", coinCount=" + coinCount +
                ", rank=" + rank +
                '}'
    }
}
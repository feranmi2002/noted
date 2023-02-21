package com.faithdeveloper.noted.data

sealed class Result(val data: Any?, val msg: String) {
    class Success(data: Any?, msg: String = "Success") :
        Result(data, msg)

    class Failure(data: Any?, msg: String = "Failure") :
        Result(data, msg)
}
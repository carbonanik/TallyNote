package com.carbondev.tallynote.repository

sealed class NumberVerification {
    class CodeSendFromServer(code: String): NumberVerification()
    class Complete(smsCode: String? = null): NumberVerification()
    object Failed: NumberVerification()
}
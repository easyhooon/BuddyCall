package kr.ac.konkuk.koogle.Model

data class ChatModel (
    val senderId: String,
    val message: String
) {
    constructor(): this("","")
}

package kr.ac.konkuk.koogle.Model

data class ChatModel (
    val writerId: String,
    val writerName: String,
    val writerProfileImageUrl: String,
    val chatContent: String,
    val chatCreatedAt: Long

) {
    constructor(): this("","","","", 0)
}
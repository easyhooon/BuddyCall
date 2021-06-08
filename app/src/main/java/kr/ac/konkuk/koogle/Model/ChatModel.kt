package kr.ac.konkuk.koogle.Model

data class ChatModel (
    var viewType: Int,
    val writerId: String,
    val writerName: String,
    val writerProfileImageUrl: String,
    val chatContent: String,
    val chatCreatedAt: Long,
    val chatId: String
) {
    constructor(): this(0,"","","","", 0, "")

//    companion object {
//        const val LEFT_CHAT = 1
//        const val RIGHT_CHAT = 2
//    }
}
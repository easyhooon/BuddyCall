package kr.ac.konkuk.koogle.Model

data class ChatModel (
    var viewType: Int, //뷰타입(내가 보낸 것과, 상대방이 보낸 것을 구분)
    val writerId: String, //작성자 고유 id
    val writerName: String, //작성자 이름
    val writerProfileImageUrl: String,//작성자 프로필 이미지 url
    val chatContent: String, //메세지 내용
    val chatCreatedAt: Long, // 메세지 작성시간
    val chatId: String //메세지의 고유 id
) {
    constructor(): this(0,"","","","", 0, "")
}
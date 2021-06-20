package kr.ac.konkuk.koogle.Model

data class CommentModel(
    val commentId: String, //평가글 고유 id
    val writerId: String, //작성자 고유 id
    val writerName: String, //작성자 이름
    val writerProfileImageUrl: String,//작성자 프로필 이미지 url
    val commentContent: String, //평가 내용
    val commentCreatedAt: Long
) {
    constructor(): this("","","","","", 0)
}

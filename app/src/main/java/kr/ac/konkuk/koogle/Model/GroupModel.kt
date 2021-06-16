package kr.ac.konkuk.koogle.Model

//todo 1대1이 아닌 1대다 채팅방으로 만들어야 함

data class GroupModel (
    val groupId: String,
    val adminId: String,
    val adminName: String,
    val adminProfileImageUrl: String,
    val articleTitle: String,
    val articleContent: String,
    val recruitmentNumber: Int
) {
    constructor(): this("","","","","","", 0)
}

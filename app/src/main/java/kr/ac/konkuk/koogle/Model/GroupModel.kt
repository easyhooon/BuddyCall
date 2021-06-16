package kr.ac.konkuk.koogle.Model

//todo 1대1이 아닌 1대다 채팅방으로 만들어야 함

data class GroupModel (
    val Users: UserModel?,
    val groupId: String,
    val adminId: String,
    val adminName: String,
    val adminProfileImageUrl: String,
    val articleTitle: String,
    val groupLastChat: String,
    val recruitmentNumber: Int,
    val currentNumber: Int,
    val groupLastChatCreatedAt: Long
) {
    constructor(): this(null,"","","","",
        "","", 0, 0, 0)
}

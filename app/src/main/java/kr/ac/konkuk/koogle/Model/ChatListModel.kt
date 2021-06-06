package kr.ac.konkuk.koogle.Model

//todo 1대1이 아닌 1대다 채팅방으로 만들어야 함

data class ChatListModel(
    val buyerId: String,
    val sellerId: String,
    val itemTitle: String,
    val key: Long
){
    constructor(): this("", "", "", 0)
}

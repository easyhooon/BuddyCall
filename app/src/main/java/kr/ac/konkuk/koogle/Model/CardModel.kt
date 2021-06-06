package kr.ac.konkuk.koogle.Model

data class CardModel(
    val userId: String,
    val userName: String,
    val profileImage: String,
    val postId: String,
    val postTitle: String,
    val postImageUrl: String,
    val postContent:String
){
    constructor(): this("","", "", "", "","","")
}


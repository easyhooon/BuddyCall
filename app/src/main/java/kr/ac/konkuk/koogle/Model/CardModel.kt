package kr.ac.konkuk.koogle.Model

data class CardModel(
    val writerId: String,
    val writerName: String,
    val writerProfileImageUrl: String,
    val articleId: String,
    val articleTitle: String,
    val articleImageUrl: String,
    val articleContent:String
){
    constructor(): this("","","", "", "", "","")
}


package kr.ac.konkuk.koogle.Model

import kr.ac.konkuk.koogle.Model.Entity.SearchResultEntity


data class ArticleModel(
    val writerId: String,
    val writerName: String,
    val writerProfileImageUrl: String,
    val articleId: String,
    val articleTitle: String,
    val articleCreatedAt: Long,
    val recruitmentNumber: Int,
    val currentNumber: Int,
    val articleContent: String,
    val articleImageUrl: ArrayList<String>,
    val desiredLocation: SearchResultEntity?,
) {

    //firebase realtime DB에서 그대로 MODEL 클래스를 사용하려면 빈 생성자가 필수로 있어야야 함
    constructor(): this("", "","","", "",
        0, 0, 0,"", arrayListOf(), null)
}
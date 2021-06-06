package kr.ac.konkuk.koogle.Model

import android.net.Uri

data class ArticleModel(
    val writerId: String,
    val writerProfileImageUrl: String,
    val articleId: String,
    val articleTitle: String,
    val articleCreatedAt: Long,
    val articleContent: String,
    val articleImageUrl: String
) {

    //firebase realtime DB에서 그대로 MODEL 클래스를 사용하려면 빈 생성자가 필수로 있어야야 함
    constructor(): this("", "","", "",0, "", "")

}
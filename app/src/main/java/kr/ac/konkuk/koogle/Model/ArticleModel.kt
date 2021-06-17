package kr.ac.konkuk.koogle.Model

import kr.ac.konkuk.koogle.Model.Entity.SearchResultEntity

data class ArticleModel(
    val writerId: String, //글 작성자 고유 id
    val writerName: String, //작성자 이름
    val writerProfileImageUrl: String, //작성자 프로필 이미지 url
    val articleId: String, //글 고유 id
    val articleTitle: String, //글 제목
    val articleCreatedAt: Long,//글 작성 시간
    val recruitmentNumber: Int,//모집 인원
    val currentNumber: Int,//현재 모집된 인원
    val articleContent: String,//글 내용
    val articleImageUrl: String,//글 이미지 url
    val desiredLocation: SearchResultEntity?,//만남 희망 장소
) {

    //firebase realtime DB에서 그대로 MODEL 클래스를 사용하려면 빈 생성자가 필수로 있어야야 함
    constructor(): this("", "","","", "",
        0, 0, 0,"", "", null)
}
package kr.ac.konkuk.koogle.Model

import kr.ac.konkuk.koogle.Model.Entity.SearchResultEntity

data class CardModel(
    val writerId: String,//작성자 고유 id
    val writerName: String,//작성자 이름
    val writerProfileImageUrl: String,
    val articleId: String, //글 고유 id
    val articleTitle: String, //글 제목
    val articleThumbnailImageUrl: String,//작성자 프로필 이미지 url
//    val articleImageUrl: String, //글 이미지
    val articleContent:String,//글 내용
    val currentNumber:Int,// 현재 모집된 인원
    val recruitmentNumber:Int, //모집 인원
    val desiredLocation: SearchResultEntity? //만남 희망 장소
){
//    constructor(): this("","","", "", "", "","",0,0, null)
    //임시 수정
    constructor(): this("","","","", "",  "","",0,0, null)
}


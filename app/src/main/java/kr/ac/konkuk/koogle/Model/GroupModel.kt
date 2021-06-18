package kr.ac.konkuk.koogle.Model

data class GroupModel (
    val Users: UserModel?, //그룹에 속한 유저들 모델
    val groupId: String, //그룹 고유 id,
    val adminId: String, //그룹장 id
    val adminName: String, //그룹장 이름
    val adminProfileImageUrl: String,//그룹장 프로필 이미지 url
    val articleTitle: String, //글 제목
    val groupLastChat: String,//그룹 채팅의 가장 마지막 채팅
    val recruitmentNumber: Int, // 모집인원
    val currentNumber: Int,//현재 모집된 인원
    val groupLastChatCreatedAt: Long //가장 마지막 채팅의 작성시간
) {
    constructor(): this(null,"","","","",
        "","", 0, 0, 0)
}

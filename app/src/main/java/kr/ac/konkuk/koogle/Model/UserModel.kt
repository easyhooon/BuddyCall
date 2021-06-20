package kr.ac.konkuk.koogle.Model

data class UserModel(
    val userId: String, //유저 고유 id
    val userEmail: String, //유저 이메일
    val userName: String, //유저 이름
    val userProfileImageUrl: String, // 유저 프로필 이미지 url
    val Groups: GroupModel?, //유저가 속한 그룹들 모델
    val Comments: CommentModel?, //유저가 받은 평가
    val blockList: BlockUserModel?
) {
    constructor() : this("", "", "", "", null, null, null)
}

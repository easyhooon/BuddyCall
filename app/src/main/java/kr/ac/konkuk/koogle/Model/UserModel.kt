package kr.ac.konkuk.koogle.Model

data class UserModel(
    val userId: String,
    val userEmail: String,
    val userName: String,
    val userProfileImageUrl: String,
    val Groups: GroupModel?
) {
    constructor() : this("", "", "", "", null)
}

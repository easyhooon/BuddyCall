package kr.ac.konkuk.koogle.Model

data class UserItem(
    val userId: String,
    val user_email: String,
    val user_name: String,
    val profile_image: String
) {
    constructor() : this("", "", "", "")
}

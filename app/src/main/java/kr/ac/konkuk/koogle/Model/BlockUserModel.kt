package kr.ac.konkuk.koogle.Model

data class BlockUserModel(
    val userId: String, //유저 고유 id
    val userName: String, //유저 이름
) {
    constructor() : this("", "")
}


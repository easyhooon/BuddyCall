package kr.ac.konkuk.koogle

class DBKeys {
    //DB reference path Key
    companion object {
        //USER DB
        const val DB_USERS = "Users"
        const val USER_ID = "userId"
        const val USER_NAME = "userName"
        const val USER_EMAIL = "userEmail"
        const val PROFILE_IMAGE = "userProfileImage"
        const val CHILD_CHAT = "chat"

        //Community
        const val DB_ARTICLES = "Articles"
        const val PHOTO_PATH = "article/photo"
        const val ARTICLE_ID = "articleId"
        const val ARTICLE_TITLE = "articleTitle"
        const val ARTICLE_CONTENT = "articleContent"
        const val ARTICLE_IMAGE_URL = "articleImageUrl"
        const val ARTICLE_CREATED_AT = "articleCreatedAt"
        const val WRITER_ID = "writerId"
        const val WRITER_PROFILE_IMAGE_URL = "writerProfileImageUrl"


        //Chatting
        const val DB_CHATS = "chat"

        const val USERS = "Users"
        const val LIKED_BY = "likedBy"
        const val LIKE = "like"
        const val DIS_LIKE = "disLike"
        const val MATCH = "match"

    }
}
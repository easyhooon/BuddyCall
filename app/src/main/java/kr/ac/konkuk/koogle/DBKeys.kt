package kr.ac.konkuk.koogle

class DBKeys {
    //DB reference path Key
    companion object {
        //USER DB
        const val DB_USERS = "Users"
        const val USER_ID = "userId"
        const val USER_NAME = "userName"
        const val USER_EMAIL = "userEmail"
        const val USER_PROFILE_IMAGE_URL = "userProfileImageUrl"

        //Community, Card
        const val DB_ARTICLES = "Articles"
        const val PHOTO_PATH = "article/photo"
        const val ARTICLE_ID = "articleId"
        const val ARTICLE_TITLE = "articleTitle"
        const val ARTICLE_CONTENT = "articleContent"
        const val ARTICLE_IMAGE_URL = "articleImageUrl"
        const val ARTICLE_CREATED_AT = "articleCreatedAt"
        const val WRITER_ID = "writerId"
        const val WRITER_NAME = "writerName"
        const val WRITER_PROFILE_IMAGE_URL = "writerProfileImageUrl"


        //Group
        const val DB_GROUPS = "Groups"
        const val GROUP_ID = "groupId"
        const val ADMIN_ID ="adminId"
        const val ADMIN_NAME ="adminName"
        const val ADMIN_PROFILE_IMAGE_URL = "adminProfileImageUrl"

        //Chat
        const val DB_MESSAGES = "Messages"
        const val CHAT_ID = "chatId"
        const val CHAT_CONTENT = "chatContent"
        const val CHAT_CREATED_AT = "chatCreatedAt"


    }
}
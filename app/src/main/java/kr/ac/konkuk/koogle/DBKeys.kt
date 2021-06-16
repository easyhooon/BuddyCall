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
        const val USER_PROFILE_IMAGE_PATH = "profile/photo"

        //Community, Card
        const val DB_ARTICLES = "Articles"
        const val ARTICLE_IMAGE_PATH = "article/photo"
        const val ARTICLE_ID = "articleId"
        const val ARTICLE_TITLE = "articleTitle"
        const val ARTICLE_CONTENT = "articleContent"
        const val ARTICLE_IMAGE_URL = "articleImageUrl"
        const val ARTICLE_CREATED_AT = "articleCreatedAt"
        const val WRITER_ID = "writerId"
        const val WRITER_NAME = "writerName"
        const val WRITER_PROFILE_IMAGE_URL = "writerProfileImageUrl"
        const val RECRUITMENT_NUMBER = "recruitmentNumber"
        const val DESIRED_LOCATION = "desiredLocation"


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

        //ViewType
        const val LEFT_CHAT = 1; //남들의 채팅(프로필과 이름 내용 모두 보임)
        const val RIGHT_CHAT = 2; //내 채팅(내용만 보임)

    }
}
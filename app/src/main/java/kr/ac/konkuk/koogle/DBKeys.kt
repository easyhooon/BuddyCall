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
        const val DB_BLOCK_USERS= "BlockUsers"

        //Community, Card
        const val DB_ARTICLES = "Articles"
        const val ARTICLE_IMAGE_PATH = "article/photo"
        const val ARTICLE_ID = "articleId"
        const val ARTICLE_TITLE = "articleTitle"
        const val ARTICLE_CONTENT = "articleContent"
        const val ARTICLE_THUMBNAIL_IMAGE_URL = "articleThumbnailImageUrl"
        const val ARTICLE_IMAGE_URL = "articleImageUrl"
        const val ARTICLE_IMAGE_FILE_NAME = "articleImageFileName"
        const val ARTICLE_CREATED_AT = "articleCreatedAt"
        const val WRITER_ID = "writerId"
        const val WRITER_NAME = "writerName"
        const val WRITER_PROFILE_IMAGE_URL = "writerProfileImageUrl"
        const val RECRUITMENT_NUMBER = "recruitmentNumber"
        const val CURRENT_NUMBER = "currentNumber"
        const val DESIRED_LOCATION = "desiredLocation"

        //Group
        const val DB_GROUPS = "Groups"
        const val GROUP_ID = "groupId"
        const val ADMIN_ID ="adminId"
        const val ADMIN_NAME ="adminName"
        const val ADMIN_PROFILE_IMAGE_URL = "adminProfileImageUrl"
        const val GROUP_LAST_CHAT = "groupLastChat"
        const val GROUP_LAST_CHAT_CREATED_AT = "groupLastChatCreatedAt"

        //Chat
        const val DB_MESSAGES = "Messages"
        const val CHAT_ID = "chatId"
        const val CHAT_CONTENT = "chatContent"
        const val CHAT_CREATED_AT = "chatCreatedAt"

        //Comment
        const val DB_COMMENTS = "Comments"
        const val COMMENT_ID = "commentId"
        const val COMMENT_CREATED_AT = "commentCreatedAt"
        const val COMMENT_CONTENT = "commentContent"

        //ViewType
        const val LEFT_CHAT = 1 //남들의 채팅(프로필과 이름 내용 모두 보임)
        const val RIGHT_CHAT = 2 //내 채팅(내용만 보임)

        // 대분류 태그
        const val DB_MAIN_TAGS = "Tag"
        // ID를 이름으로 쓰기
        const val TAG_ID = "tagId"
        const val USED = "used"
        const val USED_TAGS = "usedTags" // 이 태그가 태그 리스트로써 쓰인 횟수
        const val USED_VALUE = "usedValue" // 이 태그가 수치로써 쓰인 횟수
        const val SUB_TAGS = "subTag"

        // 유저-태그정보
        const val DB_USER_TAG = "UserTags"
        const val TAG_INDEX = "index"
        const val TAG_TYPE = "tagType"
        const val TAG_VALUE = "tagValue"
    }
}
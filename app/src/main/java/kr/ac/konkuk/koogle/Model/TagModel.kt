package kr.ac.konkuk.koogle.Model

import java.io.Serializable

// 태그 밑에 또 태그들이 있는 경우: TAG, 수치 값이 있는 경우: VALUE
object TagType {
    const val TAG = 0
    const val VALUE = 1
}
data class TagModel(
    var main_tag_name: String,
    var sub_tag_list: ArrayList<String>,
    var value: Int,
    var tag_type: Int) : Serializable
{
    constructor(): this("", ArrayList(), 0, 0)
    constructor(main_tag_name: String, tag_type: Int): this(main_tag_name, ArrayList(), 0, tag_type)
}
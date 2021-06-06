package kr.ac.konkuk.koogle.Model

import java.io.Serializable

data class TagModel(
    var main_tag_name: String,
    var sub_tag_list: ArrayList<String>) : Serializable
{
    constructor(): this("", ArrayList())
}
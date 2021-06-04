package kr.ac.konkuk.koogle.Model

import java.io.Serializable

data class TagItem(var main_tag_name: String, var sub_tag_list: ArrayList<String>) : Serializable {

}
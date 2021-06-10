package kr.ac.konkuk.koogle

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import kr.ac.konkuk.koogle.Activity.AddArticleActivity
import kr.ac.konkuk.koogle.databinding.OptionDialogBinding

class OptionDialog(context: Context) : Dialog(context) {

    lateinit var binding: OptionDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = OptionDialogBinding.inflate(layoutInflater)

//        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)

        binding.editTextView.setOnClickListener {
//            Toast.makeText(context, "글 수정하기", Toast.LENGTH_SHORT).show()
            val intent = Intent(context, AddArticleActivity::class.java)
        }

        binding.deleteTextView.setOnClickListener {
            Toast.makeText(context, "글 삭제하기", Toast.LENGTH_SHORT).show()
        }
    }
}
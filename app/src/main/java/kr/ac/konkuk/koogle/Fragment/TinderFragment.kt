package kr.ac.konkuk.koogle.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.ac.konkuk.koogle.ProfileActivity
import kr.ac.konkuk.koogle.R
import kr.ac.konkuk.koogle.databinding.FragmentTinderFragmentBinding


class TinderFragment : Fragment() {

    var binding: FragmentTinderFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTinderFragmentBinding.inflate(layoutInflater, container, false)

        initProfileButton()

        return binding!!.root
    }
    private fun initProfileButton() {
        binding?.ProfileImage?.setOnClickListener {
            val intent = Intent(context, ProfileActivity::class.java)
            startActivity(intent)
        }
    }
}
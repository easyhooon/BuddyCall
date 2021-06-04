package kr.ac.konkuk.koogle.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.ac.konkuk.koogle.databinding.FragmentGroupBinding


class GroupFragment : Fragment() {

    var binding :FragmentGroupBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentGroupBinding.inflate(layoutInflater, container, false)

        return binding!!.root
    }

}
package kr.ac.konkuk.koogle.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DatabaseReference
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.Direction
import kr.ac.konkuk.koogle.Adapter.CardItemAdapter
import kr.ac.konkuk.koogle.databinding.FragmentCardBinding


class CardFragment : Fragment(), CardStackListener {

    var binding: FragmentCardBinding? = null
    private val adapter = CardItemAdapter()
    private lateinit var userDB: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCardBinding.inflate(layoutInflater, container, false)

        initDB()
        initCardStackView()

        return binding!!.root
    }

    private fun initDB() {

    }

    private fun initCardStackView() {
        binding?.cardStackView?.layoutManager = CardStackLayoutManager(context, this)
        binding?.cardStackView?.adapter = adapter
    }

    override fun onCardSwiped(direction: Direction?) {

    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {}

    override fun onCardRewound() {}

    override fun onCardCanceled() {}

    override fun onCardAppeared(view: View?, position: Int) {}

    override fun onCardDisappeared(view: View?, position: Int) {}
}
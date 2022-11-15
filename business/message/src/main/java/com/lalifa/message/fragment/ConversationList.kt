package com.lalifa.message.fragment

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.lalifa.message.R
import io.rong.imkit.conversationlist.ConversationListFragment

class ConversationList : ConversationListFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRefreshLayout.setBackgroundColor(ContextCompat.getColor(
            requireActivity(),R.color.black))
    }
}
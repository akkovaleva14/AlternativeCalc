package com.example.logger

import android.os.Bundle
import androidx.fragment.app.Fragment

open class FragmentLogger : Fragment() {
    private val tagName = "${this::class.simpleName}Tag"
    private val fragmentName = "${this::class.simpleName}"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.i(tagName, "$fragmentName created")
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        Logger.i(tagName, "$fragmentName view state restored")
    }

    override fun onStart() {
        super.onStart()
        Logger.i(tagName, "$fragmentName started")
    }

    override fun onResume() {
        super.onResume()
        Logger.i(tagName, "$fragmentName resumed")
    }

    override fun onPause() {
        super.onPause()
        Logger.i(tagName, "$fragmentName paused")
    }

    override fun onStop() {
        super.onStop()
        Logger.i(tagName, "$fragmentName stopped")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Logger.i(tagName, "$fragmentName saved instance state")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Logger.i(tagName, "$fragmentName view destroyed")
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger.i(tagName, "$fragmentName destroyed")
    }

    override fun onDetach() {
        super.onDetach()
        Logger.i(tagName, "$fragmentName detached")
    }
}
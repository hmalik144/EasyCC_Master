package com.appttude.h_mal.easycc.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.appcompat.app.AppCompatActivity
import com.appttude.h_mal.easycc.R
import com.appttude.h_mal.easycc.utils.*

abstract class BaseActivity<V : BaseViewModel> : AppCompatActivity() {

    private lateinit var loadingView: View

    abstract val viewModel: V?

    private var loading: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureObserver()
    }

    /**
     *  Creates a loading view which to be shown during async operations
     *
     *  #setOnClickListener(null) is an ugly work around to prevent under being clicked during
     *  loading
     */
    private fun instantiateLoadingView() {
        loadingView = layoutInflater.inflate(R.layout.progress_layout, null)
        loadingView.setOnClickListener(null)
        addContentView(loadingView, LayoutParams(MATCH_PARENT, MATCH_PARENT))

        loadingView.hide()
    }

    override fun onStart() {
        super.onStart()
        instantiateLoadingView()
    }

    fun <A : AppCompatActivity> startActivity(activity: Class<A>) {
        val intent = Intent(this, activity)
        startActivity(intent)
    }

    open fun onStarted() {
        loadingView.fadeIn()
        loading = true
    }

    /**
     *  Called in case of success or some data emitted from the liveData in viewModel
     */
    open fun onSuccess(data: Any?) {
        loadingView.fadeOut()
        loading = false
    }

    /**
     *  Called in case of failure or some error emitted from the liveData in viewModel
     */
    open fun onFailure(error: String?) {
        error?.let { displayToast(it) }
        loadingView.fadeOut()
        loading = false
    }

    private fun configureObserver() {
        viewModel?.uiState?.observe(this) {
            when (it) {
                is ViewState.HasStarted -> onStarted()
                is ViewState.HasData<*> -> onSuccess(it.data.getContentIfNotHandled())
                is ViewState.HasError -> onFailure(it.error.getContentIfNotHandled())
            }
        }
    }

    private fun View.fadeIn() = apply {
        show()
        triggerAnimation(android.R.anim.fade_in) {}
    }

    private fun View.fadeOut() = apply {
        hide()
        triggerAnimation(android.R.anim.fade_out) {}
    }


    override fun onBackPressed() {
        if (!loading) super.onBackPressed()
    }

}
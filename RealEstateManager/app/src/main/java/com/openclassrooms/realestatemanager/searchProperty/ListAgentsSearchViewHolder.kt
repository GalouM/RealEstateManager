package com.openclassrooms.realestatemanager.searchProperty

import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.entity.Agent
import java.lang.ref.WeakReference

/**
 * Created by galou on 2019-08-29
 */
class ListAgentsSearchViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    @BindView(R.id.list_agent_search_rv_picture) lateinit var pictureAgent: ImageView
    @BindView(R.id.list_agent_search_rv_name) lateinit var nameAgent: TextView
    @BindView(R.id.list_agent_search_check) lateinit var checkBox: CheckBox

    private var callbackWeakRef: WeakReference<ListAgentSearchAdapter.ListenerCheckBox>? = null
    private var agent: Agent? = null

    init {
        ButterKnife.bind(this, view)
    }

    fun updateWithAgent(
            agent: Agent, glide: RequestManager,
            callback: ListAgentSearchAdapter.ListenerCheckBox
    ){
        callbackWeakRef = WeakReference(callback)
        this.agent = agent

        val nameToDisplay = "${agent.firstName} ${agent.lastName}"
        nameAgent.text = nameToDisplay

        agent.urlProfilePicture?.let {
            glide.load(it).apply(RequestOptions.circleCropTransform()).into(pictureAgent)
        }
    }

    @OnClick(R.id.list_agent_search_check)
    fun onClickCheckbox(checkBox: CheckBox){
        val callback = callbackWeakRef?.get()
        callback?.let { callback.onClickCheckBox(agent!!.id!!, checkBox.isChecked) }
    }

    fun selectOnSelectAll(){
        checkBox.isChecked = true
    }
}
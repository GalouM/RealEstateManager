package com.openclassrooms.realestatemanager.searchProperty

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.entity.Agent

/**
 * Created by galou on 2019-08-29
 */
class ListAgentSearchAdapter(
        var agents: List<Agent>, private val glide: RequestManager, val callback: ListenerCheckBox
) :
        RecyclerView.Adapter<ListAgentsSearchViewHolder>(){

    private val holders = mutableListOf<ListAgentsSearchViewHolder>()

    interface ListenerCheckBox{
        fun onClickCheckBox(id: String, isChecked: Boolean)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListAgentsSearchViewHolder {
        val context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.list_agent_search_view_item, parent, false)

        return ListAgentsSearchViewHolder(view)
    }

    override fun getItemCount(): Int {
        return agents.size
    }

    override fun onBindViewHolder(holder: ListAgentsSearchViewHolder, position: Int) {
        holder.updateWithAgent(agents[position], glide, callback)
        holders.add(holder)
    }

    fun selectAllAgents(){
        holders.forEach { it.selectOnSelectAll() }

    }

}
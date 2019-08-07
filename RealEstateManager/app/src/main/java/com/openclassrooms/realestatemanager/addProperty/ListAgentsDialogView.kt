package com.openclassrooms.realestatemanager.addProperty


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.entity.Agent
import com.openclassrooms.realestatemanager.utils.ItemClickSupport
import java.lang.ClassCastException


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 *
 */
class ListAgentsDialogView(private val agents: List<Agent>) : DialogFragment() {

    interface OnAgentSelected{
        fun onAgentSelected(agent: Agent)
    }

    private lateinit var callback: OnAgentSelected

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ListAgentDialogAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createCallbackToParent()
    }

    private fun createCallbackToParent() {
        try {
            callback = targetFragment as OnAgentSelected
        } catch (e: ClassCastException){
            throw ClassCastException(e.toString() + "must implement OnAgentSelected")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_list_agents_dialog_view, container, false)
        recyclerView = view.findViewById(R.id.list_agent_dialog_recyclerview)
        configureRecyclerView()
        configureClickRecyclerView()

        return view
    }

    private fun configureRecyclerView(){
        adapter = ListAgentDialogAdapter(agents, Glide.with(this))
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)
    }

    private fun configureClickRecyclerView(){
        ItemClickSupport.addTo(recyclerView, R.layout.list_agent_dialog_item)
                .setOnItemClickListener{ recyclerView, position, v ->  setAgentSelected(adapter.getAgent(position), v)}
    }

    private fun setAgentSelected(agent: Agent, view: View){
        callback.onAgentSelected(agent)
        dismiss()


    }


}

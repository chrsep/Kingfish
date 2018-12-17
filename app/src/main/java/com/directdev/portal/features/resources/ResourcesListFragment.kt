package com.directdev.portal.features.resources

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.directdev.portal.R
import org.jetbrains.anko.ctx

/**-------------------------------------------------------------------------------------------------
 * Created by chris on 10/21/17.
 *------------------------------------------------------------------------------------------------*/
class ResourcesListFragment : Fragment() {
    companion object {
        fun newInstance(courseName: String, classNumber: Int): ResourcesListFragment {
            val fragment = ResourcesListFragment()
            val bundle = Bundle()
            bundle.putInt("classNumb", classNumber)
            bundle.putString("courseName", courseName)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): View {

        val presenter = (parentFragment as ResourcesFragment).presenter
        val view = inflater.inflate(R.layout.fragment_resources_list, container, false)
        val recyclerView = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.resourceRecyclerView)
        val resources = presenter.getResources(arguments.getInt("classNumb"))
        val outlineMap = resources?.resources?.map { it.courseOutlineTopicID }?.toSet()
        val resourceEmptyPlaceholder = view.findViewById<ConstraintLayout>(R.id.resourceEmptyPlaceholder)
        view.findViewById<TextView>(R.id.courseName).text = arguments.getString("courseName")
        view.findViewById<Button>(R.id.refreshResourceButton).setOnClickListener {
            presenter.sync()
        }
        resourceEmptyPlaceholder.visibility = if (outlineMap != null)
            View.GONE
        else
            View.VISIBLE
        recyclerView.visibility = if (outlineMap != null)
            View.VISIBLE
        else
            View.GONE

        if (outlineMap != null) {
            recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(ctx)
            recyclerView.adapter = ResourcesRecyclerAdapter(ctx, outlineMap.toList(), resources)
        }
        return view
    }
}
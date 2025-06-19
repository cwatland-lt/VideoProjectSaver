package com.yourcompany.videoprojectsaver

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.videoprojectsaver.Project
import com.example.videoprojectsaver.R

class ProjectAdapter(private val projects: MutableList<Project>) :
    RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>() {

    // Define a listener for item clicks (optional, but good practice)
    var onItemClick: ((Project) -> Unit)? = null

    // Inner class that holds the views for a single list item
    class ProjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val projectNameTextView: TextView = itemView.findViewById(R.id.tv_projectNameItem)
    }

    // Called when RecyclerView needs a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_project, parent, false)
        return ProjectViewHolder(view)
    }

    // Called to bind data to a ViewHolder
    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val project = projects[position]
        holder.projectNameTextView.text = project.name

        // Set up the click listener for the entire item view
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(project)
        }
    }

    // Returns the total number of items in the list
    override fun getItemCount(): Int = projects.size

    // Method to update the data in the adapter
    fun updateProjects(newProjects: List<Project>) {
        projects.clear()
        projects.addAll(newProjects)
        notifyDataSetChanged() // Notifies the RecyclerView that the data has changed
    }

    // Method to add a single project (useful when adding new projects from UI)
    fun addProject(project: Project) {
        projects.add(project)
        notifyItemInserted(projects.size - 1) // More efficient update
    }
}
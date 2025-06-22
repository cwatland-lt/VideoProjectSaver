package com.example.videoprojectsaver

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yourcompany.videoprojectsaver.ProjectAdapter
import java.io.File // Import for File operations

class MainActivity : AppCompatActivity() {

    // Declare UI elements
    private lateinit var etProjectName: EditText
    private lateinit var btnAddProject: Button
    private lateinit var rvProjects: RecyclerView

    // Data for our projects list
    private val projects = mutableListOf<Project>()
    private lateinit var projectAdapter: ProjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize UI elements by finding them by their IDs
        etProjectName = findViewById(R.id.et_projectName)
        btnAddProject = findViewById(R.id.btn_addProject)
        rvProjects = findViewById(R.id.rv_projects)

        // Setup RecyclerView
        projectAdapter = ProjectAdapter(projects) // Initialize adapter with our list
        rvProjects.layoutManager = LinearLayoutManager(this) // Use a linear layout for items
        rvProjects.adapter = projectAdapter // Set the adapter to the RecyclerView

        // Handle Add Project button click
        btnAddProject.setOnClickListener {
            addProject()
        }

        // (Optional for now) Handle project item clicks
        projectAdapter.onItemClick = { project ->
            Toast.makeText(this, "Opening project: ${project.name}", Toast.LENGTH_SHORT).show()
            // TODO: In a later step, we'll navigate to a detail screen for this project
        }

        // Load existing projects when the app starts
        loadExistingProjects()
    }

    private fun addProject() {
        val projectName = etProjectName.text.toString().trim()

        if (projectName.isEmpty()) {
            Toast.makeText(this, "Please enter a project name", Toast.LENGTH_SHORT).show()
            return
        }

        // Sanitize project name for folder creation (replace invalid characters)
        val sanitizedProjectName = projectName.replace("[^a-zA-Z0-9.-]".toRegex(), "_")

        // Define the base directory for our projects
        // Using getExternalFilesDir() is recommended for app-specific files,
        // as it doesn't require extra runtime permissions on modern Android versions.
        val baseDir = getExternalFilesDir(null) // Or Environment.DIRECTORY_MOVIES etc. if you want public
        if (baseDir == null) {
            Toast.makeText(this, "External storage not available", Toast.LENGTH_LONG).show()
            return
        }

        val projectFolder = File(baseDir, "VideoProjects/$sanitizedProjectName")

        if (projectFolder.exists()) {
            Toast.makeText(this, "Project with this name already exists!", Toast.LENGTH_SHORT).show()
            return
        }

        val success = projectFolder.mkdirs() // Creates the directory and any necessary parent directories

        if (success) {
            val newProject = Project(projectName, projectFolder)
            projectAdapter.addProject(newProject)
            etProjectName.text.clear() // Clear the input field
            Toast.makeText(this, "Project '$projectName' created!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Failed to create project folder. Check permissions/storage.", Toast.LENGTH_LONG).show()
        }
    }

    private fun loadExistingProjects() {
        val baseDir = getExternalFilesDir(null) // Same base directory as for adding projects

        if (baseDir != null) {
            val projectsDir = File(baseDir, "VideoProjects")
            if (projectsDir.exists() && projectsDir.isDirectory) {
                // List subdirectories within "VideoProjects"
                val existingFolders = projectsDir.listFiles { file -> file.isDirectory }

                if (existingFolders != null) {
                    val loadedProjects = mutableListOf<Project>()
                    for (folder in existingFolders) {
                        // Use the folder name as the project name for simplicity
                        loadedProjects.add(Project(folder.name, folder))
                    }
                    projects.clear()
                    projects.addAll(loadedProjects)
                    projectAdapter.notifyDataSetChanged() // Update the RecyclerView
                }
            }
        }
    }
}
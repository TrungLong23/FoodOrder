package com.example.ecofood.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecofood.adapter.MenuAdapter
import com.example.ecofood.databinding.FragmentSearchBinding
import com.example.ecofood.model.MenuItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: MenuAdapter
    private lateinit var database: FirebaseDatabase
    private val originalMenuItems = mutableListOf<MenuItem>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        // retrieve menu item from database
        retrieveMenuItem()

        setupSearchView()


        return binding.root
    }

    private fun retrieveMenuItem() {
        // get database ref
        database = FirebaseDatabase.getInstance()
        // ref to the Menu node
        val foodReference: DatabaseReference = database.reference.child("Menu")
        foodReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapShot in snapshot.children){
                   val menuItem = foodSnapShot.getValue(MenuItem::class.java)
                   menuItem?.let {
                       originalMenuItems.add(it)
                   }
                   showAllMenu()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun showAllMenu() {

        val filteredMenuItem = ArrayList(originalMenuItems)
        setAdapter(filteredMenuItem)


    }

    private fun setAdapter(filteredMenuItem: List<MenuItem>) {
        adapter = MenuAdapter(filteredMenuItem, requireContext())
        binding.menuSearchRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.menuSearchRecyclerView.adapter = adapter
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                filterMenuItems(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filterMenuItems(newText)
                return true
            }
        })
    }

    private fun filterMenuItems(query: String) {
        val filteredMenuItems = originalMenuItems.filter {
            it.foodName?.contains(query, ignoreCase = true) == true
        }
        setAdapter(filteredMenuItems)
    }

}


package com.example.foldablesdemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.slidingpanelayout.widget.SlidingPaneLayout


class MainActivity : AppCompatActivity() {
    lateinit var paneView: SlidingPaneLayout
    lateinit var detailView: FrameLayout

    /**
     * Adapter used for our recycler view which holds the items in the sliding pane.
     */
    private class TextItemAdapter(
        private val items: List<String>,
        private val onSelect: (String) -> Unit
    ) :  RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            LayoutInflater.from(parent.context)
                // Instead of implementing a new layout, reuse a convenient one from the
                // standard `android.R.layout` provided by the platform.
                .inflate(android.R.layout.simple_list_item_1, parent, false)
                // Instantiate our simple ViewHolder and return it.
                .let { ItemViewHolder(it) }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val item = items[position]

            // Set the item's text according to the corresponding list item.
            (holder.itemView as TextView).text = item

            // Here we pass-through the selector callback as the click listener for each item.
            holder.itemView.setOnClickListener { onSelect(item) }
        }

        override fun getItemCount(): Int = items.size
    }

    /**
     * No need to implement internal details since the view is just a TextView.
     */
    private class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Cache view items upon layout inflate.
        paneView = findViewById(R.id.sliding_pane_layout)
        detailView = findViewById(R.id.detail_container)
        val recyclerView = findViewById<RecyclerView>(R.id.list_pane)

        val itemList = listOf("Item 1", "Item 2", "Item 3", "Item 4")
        recyclerView.layoutManager = LinearLayoutManager(baseContext, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = TextItemAdapter(itemList) {
            // When an item is selected, open the detail view and display the selected item.
            paneView.open()  // WARNING: SlidingPaneLayout.open() will close the *left* pane.
            detailView.findViewById<TextView>(R.id.item_text).text = it
        }
    }

    /**
     * Override the back pressed button to show the item selector pane instead of back-navigation.
     */
    override fun onBackPressed() {
        paneView.close()  // WARNING: SlidingPaneLayout.close() will open the *left* pane.
    }

}
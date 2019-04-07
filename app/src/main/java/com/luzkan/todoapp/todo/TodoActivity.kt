package com.luzkan.todoapp.todo

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.luzkan.todoapp.R
import com.luzkan.todoapp.data.local.TodoListDatabase
import com.luzkan.todoapp.data.local.models.Todo
import kotlinx.android.synthetic.main.activity_main.*

class TodoActivity : AppCompatActivity(), TodoAdapter.OnTodoItemClickedListener{

    private var todoDatabase: TodoListDatabase? = null
    private var todoAdapter: TodoAdapter? = null

    private var sortBy = R.id.sort_id
    // There are for sorting with reverse
    private var rvrsAdded = true
    private var rvrsPrior = true
    private var rvrsTodo = true
    private var rvrsTitle = true
    private var restored = true
    private var swap = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        todoDatabase = TodoListDatabase.getInstance(this)
        todoAdapter = TodoAdapter()
        todoAdapter?.setTodoItemClickedListener(this)

        // Action button that moves user to adding interface
        add_todo.setOnClickListener{
            swap = true
            startActivity(Intent(this, AddTodoActivity::class.java))
        }

        // Quick Add button in the main menu
        addTodoQuick.setOnClickListener{

            // Unlike AddTodoActivity, here if someone just presses a button, it can as well do nothing
            // As that behaviour is expected by user
            if(!(titleQuick == null || titleQuick.text.toString() == "")) {
                val todo = Todo(titleQuick.text.toString(), "", 1, null, "")
                todoDatabase!!.getTodo().saveTodo(todo)
                titleQuick.setText("")
            }
            // Resume must be called to refresh main screen by getting what we just put into Database
            swap = true
            onResume()
        }

        // Restore the way the list is sorted after app was closed
        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        rvrsAdded = !sharedPref.getBoolean("rvrsAddedBool", true)
        rvrsPrior = !sharedPref.getBoolean("rvrsPriorBool", true)
        rvrsTodo = !sharedPref.getBoolean("rvrsTodoBool", true)
        rvrsTitle = !sharedPref.getBoolean("rvrsTitleBool", true)
        sortBy = sharedPref.getInt("savedTypeOfSortPause", R.id.sort_id)
        restored = true
    }

    // Loads data back and sets adapter to ListView
    override fun onResume() {
        super.onResume()
        // Restored makes the one-time switch to true so after restoring fex: priority it doesn't do nothing when user tries to do same sort trough menu on first try
        if (restored) sortedList(sortBy, true, swap)
        else sortedList(sortBy, false, swap)

        todoMainList.adapter = todoAdapter
        todoMainList.layoutManager = LinearLayoutManager(this)
        todoMainList.hasFixedSize()
        swap = false
    }

    // Keep the way the list is sorted after rotation
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("rvrsAddedBool", rvrsAdded)
        outState.putBoolean("rvrsPriorBool", rvrsPrior)
        outState.putBoolean("rvrsTodoBool", rvrsTodo)
        outState.putBoolean("rvrsTitleBool", rvrsTitle)
        outState.putInt("savedTypeOfSort", sortBy)
        restored = true
    }

    // And restore it
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        rvrsAdded = !savedInstanceState.getBoolean("rvrsAddedBool")
        rvrsPrior = !savedInstanceState.getBoolean("rvrsPriorBool")
        rvrsTodo = !savedInstanceState.getBoolean("rvrsTodoBool")
        rvrsTitle = !savedInstanceState.getBoolean("rvrsTitleBool")
        sortBy = savedInstanceState.getInt("savedTypeOfSort")
    }

    // Same but after app closure
    override fun onPause() {
        super.onPause()
        Log.i("F", "onPause")
        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putBoolean("rvrsAddedBool", rvrsAdded)
            putBoolean("rvrsPriorBool", rvrsPrior)
            putBoolean("rvrsTodoBool", rvrsTodo)
            putBoolean("rvrsTitleBool", rvrsTitle)
            putInt("savedTypeOfSortPause", sortBy)
            apply()
        }
    }

    // Pressing task moves user to edit screen
    override fun onTodoItemClicked(todo: Todo) {
        val intent = Intent(this, AddTodoActivity::class.java)
        intent.putExtra("tId", todo.tId)
        intent.putExtra("title", todo.title)
        intent.putExtra("priority", todo.priority)
        intent.putExtra("description", todo.description)
        intent.putExtra("date", todo.date)
        intent.putExtra("time", todo.time)
        swap = true
        startActivity(intent)
    }

    // Creates a prompt that allows to (edit/delete) holden task
    override fun onTodoItemLongClicked(todo: Todo) {
        val dialoglist = arrayOf("Edit", "Delete")
        val alertDialog = AlertDialog.Builder(this).setItems(dialoglist) {
                dialog, which -> if (which==0) {
            val intent = Intent(this@TodoActivity, AddTodoActivity::class.java)
            intent.putExtra("tId", todo.tId)
            intent.putExtra("title", todo.title)
            intent.putExtra("priority", todo.priority)
            intent.putExtra("description", todo.description)
            intent.putExtra("date", todo.date)
            intent.putExtra("time", todo.time)
            swap = true
            startActivity(intent)
        }else{
            swap = true
            todoDatabase?.getTodo()?.removeTodo(todo)
            onResume()
        }
            dialog.dismiss()
        }.create()

        alertDialog.show()
    }

    // Menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu (adds items to the action bar if it is present)
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        sortBy = item.itemId

        if (sortBy == R.id.menu_deleteall) {
            // Creates an alert if an action is possible for quality of life
            if(todoDatabase?.getTodo()?.getTodoList().isNullOrEmpty()){
                Toast.makeText(applicationContext,"You've got no todo's.",Toast.LENGTH_SHORT).show()
            }else{
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Clear all Todo's")
                builder.setMessage("Do you really want to clear all todo's?")
                builder.setIcon(android.R.drawable.ic_dialog_alert)
                builder.setPositiveButton("Yes"){ _, _ ->
                    for(todo in todoDatabase?.getTodo()?.getTodoList()!!) {
                        todoDatabase?.getTodo()?.removeTodo(todo)
                    }
                    Toast.makeText(applicationContext,"Cleared all todo's.",Toast.LENGTH_SHORT).show()
                    sortBy = R.id.sort_id
                    todoAdapter?.todoList = todoDatabase?.getTodo()?.getTodoList()
                    onResume()
                }
                builder.setNeutralButton("Cancel"){ _, _ ->
                }
                val alertDialog: AlertDialog = builder.create()
                alertDialog.setCancelable(false)
                alertDialog.show()
            }
            return true
        }
        // Just type in "sortedBy = X" and "onResume()" upon fix of queries in TodoInterface and remove all the junk
        // @Update: It seems that it's not possible in a easy way due to prevention of SQL Injection.
        //          Changed the code so it's actually clean(-ish)
        sortedList(sortBy, true, false)
        todoMainList.adapter = todoAdapter
        todoMainList.layoutManager = LinearLayoutManager(this)
        return super.onOptionsItemSelected(item)
    }

    // Switch is so that we get the reverse of list only upon sorting from options, not every time we go back to main activity
    private fun sortedList(id: Int, switch: Boolean, swap: Boolean){
        if (id == R.id.sort_id) {
            if (swap) rvrsAdded = !rvrsAdded
            if(rvrsAdded) todoAdapter?.todoList = todoDatabase?.getTodo()?.getTodoList()
            else todoAdapter?.todoList = todoDatabase?.getTodo()?.getTodoListR()
            if (switch) rvrsAdded = !rvrsAdded
        }
        if (id == R.id.sort_priority) {
            if (swap) rvrsPrior = !rvrsPrior
            if (rvrsPrior) todoAdapter?.todoList = todoDatabase?.getTodo()?.getTodoListPrior()
            else todoAdapter?.todoList = todoDatabase?.getTodo()?.getTodoListPriorR()
            if (switch) rvrsPrior = !rvrsPrior
        }
        if (id == R.id.sort_date) {
            if (swap) rvrsTodo = !rvrsTodo
            if (rvrsTodo) todoAdapter?.todoList = todoDatabase?.getTodo()?.getTodoListDate()
            else todoAdapter?.todoList = todoDatabase?.getTodo()?.getTodoListDateR()
            if (switch) rvrsTodo = !rvrsTodo
        }
        if (id == R.id.sort_title) {
            if (swap) rvrsTitle = !rvrsTitle
            if (rvrsTitle) todoAdapter?.todoList = todoDatabase?.getTodo()?.getTodoListTitle()
            else todoAdapter?.todoList = todoDatabase?.getTodo()?.getTodoListTitleR()
            if (switch) rvrsTitle = !rvrsTitle
        }
    }
}
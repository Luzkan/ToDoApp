package com.luzkan.todoapp.todo

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
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
    // I'm unsure if sortedBy should be remembered in onPause() so it's the same after app closure
    // That's one of the things that would be cool to change after some feedback from users
    private var sortedBy = "tId"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        todoDatabase = TodoListDatabase.getInstance(this)
        todoAdapter = TodoAdapter()
        todoAdapter?.setTodoItemClickedListener(this)

        // Action button that moves user to adding interface
        add_todo.setOnClickListener { startActivity(Intent(this, AddTodoActivity::class.java)) }

        // Quick Add button in the main menu
        addTodoQuick.setOnClickListener{

            // Unlike AddTodoActivity, here if someone just presses a button, it can as well do nothing
            // As that behaviour is expected by user
            if(!(titleQuick == null || titleQuick.text.toString() == "")) {
                val todo = Todo(titleQuick.text.toString(), "", 1, "", "")
                todoDatabase!!.getTodo().saveTodo(todo)
                titleQuick.setText("")
            }
            // Resume must be called to refresh main screen by getting what we just put into Database
            onResume()
        }
    }

    // Loads data back and sets adapter to ListView
    override fun onResume() {
        super.onResume()
        todoAdapter?.todoList = todoDatabase?.getTodo()?.getTodoListSorted(sortedBy)
        todoMainList.adapter = todoAdapter
        todoMainList.layoutManager = LinearLayoutManager(this)
        todoMainList.hasFixedSize()
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
            startActivity(intent)
        }else{
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

    var rvrsDAdded = true
    var rvrsPrior = true
    var rvrsDTodo = true
    var rvrsName = true

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.menu_deleteall) {
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
                        onResume()
                    }
                    Toast.makeText(applicationContext,"Cleared all todo's.",Toast.LENGTH_SHORT).show()
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
        if (id == R.id.sort_id) {
            if(rvrsDAdded) {
                todoAdapter?.todoList = todoDatabase?.getTodo()?.getTodoList()
                rvrsDAdded = false
            }else{
                todoAdapter?.todoList = todoDatabase?.getTodo()?.getTodoListR()
                rvrsDAdded = true
            }
        }
        if (id == R.id.sort_priority) {
            if(rvrsPrior) {
                todoAdapter?.todoList = todoDatabase?.getTodo()?.getTodoListPriorR()
                rvrsPrior = false
            }else{
                todoAdapter?.todoList = todoDatabase?.getTodo()?.getTodoListPrior()
                rvrsPrior = true
            }
        }
        if (id == R.id.sort_date) {
            if(rvrsDTodo) {
                todoAdapter?.todoList = todoDatabase?.getTodo()?.getTodoListDateR()
                rvrsDTodo = false
            }else{
                todoAdapter?.todoList = todoDatabase?.getTodo()?.getTodoListDate()
                rvrsDTodo = true
            }
        }
        if (id == R.id.sort_title) {
            if(rvrsDTodo) {
                todoAdapter?.todoList = todoDatabase?.getTodo()?.getTodoListDateR()
                rvrsDTodo = false
            }else{
                todoAdapter?.todoList = todoDatabase?.getTodo()?.getTodoListDate()
                rvrsDTodo = true
            }
        }
        todoMainList.adapter = todoAdapter
        todoMainList.layoutManager = LinearLayoutManager(this)

        return super.onOptionsItemSelected(item)
    }
}

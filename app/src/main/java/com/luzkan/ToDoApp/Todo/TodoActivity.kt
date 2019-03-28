package com.luzkan.ToDoApp.Todo

import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import com.luzkan.ToDoApp.R
import com.luzkan.ToDoApp.data.local.TodoListDatabase
import com.luzkan.ToDoApp.data.local.models.Todo
import kotlinx.android.synthetic.main.activity_main.*

class TodoActivity : AppCompatActivity(), TodoAdapter.OnTodoItemClickedListener{


    private var todoDatabase: TodoListDatabase? = null
    private var todoAdapter: TodoAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        todoDatabase = TodoListDatabase.getInstance(this)
        todoAdapter = TodoAdapter()
        todoAdapter?.setTodoItemClickedListener(this)

        // Action button that moves user to adding interface
        add_todo.setOnClickListener { startActivity(Intent(this, AddTodoActivity::class.java)) }

        // Quick button in the main menu
        addTodoQuick.setOnClickListener{

            // Unlike AddTodoActivity, here if someone just presses a button, it can as well do nothing
            // As that behaviour is expected by user
            if(!(titleQuick == null || titleQuick.text.toString() == "")) {
                val todo = Todo(titleQuick.text.toString(), "", 1)
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
        todoAdapter?.todoList=todoDatabase?.getTodo()?.getTodoList()
        todo_rv.adapter = todoAdapter
        todo_rv.layoutManager = LinearLayoutManager(this)
        todo_rv.hasFixedSize()
    }

    // Pressing task moves user to edit screen
    override fun onTodoItemClicked(todo: Todo) {
        val intent = Intent(this, AddTodoActivity::class.java)
        intent.putExtra("tId", todo.tId)
        intent.putExtra("title", todo.title)
        intent.putExtra("priority", todo.priority)
        intent.putExtra("description", todo.description)
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
            intent.putExtra("detail", todo.description)
            startActivity(intent)
        }else{
            todoDatabase?.getTodo()?.removeTodo(todo)
            onResume()
        }
            dialog.dismiss()
        }.create()

        alertDialog.show()
    }
}

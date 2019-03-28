package com.luzkan.ToDoApp.Todo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.RadioGroup
import com.luzkan.ToDoApp.R
import com.luzkan.ToDoApp.data.local.TodoListDatabase
import com.luzkan.ToDoApp.data.local.models.Todo
import kotlinx.android.synthetic.main.activity_addtodo.*

class AddTodoActivity: AppCompatActivity(), RadioGroup.OnCheckedChangeListener{

    private var todoDatabase: TodoListDatabase? = null
    private var priority = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addtodo)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        todoDatabase = TodoListDatabase.getInstance(this)
        radioGroup.setOnCheckedChangeListener(this)


        // Moves to new screen in which user can update or create new task
        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")
        if (title == null || title == ""){
            addTodo.setOnClickListener{
                // App bugs out permanently if user tries to do dumb stuff
                // Could be TODO: Replaced with alert
                if(title_ed == null || title_ed.text.toString() == "") {
                    finish()
                }else {
                    val todo = Todo(title_ed.text.toString(), descriptionNew.text.toString(), priority)
                    todoDatabase!!.getTodo().saveTodo(todo)
                }
                finish()
            }
        }else{
            addTodo.text = getString(R.string.update)
            val tId = intent.getIntExtra("tId", 0)
            title_ed.setText(title)

            // It crashes if it tries to put null into a editView (which should be string)
            if (description != null || description != "")
                descriptionNew.setText(description)

            addTodo.setOnClickListener {
                if(title_ed == null || title_ed.text.toString() == "") {
                    finish()
                }else {
                    val todo = Todo(title_ed.text.toString(), descriptionNew.text.toString(), priority, tId)
                    todoDatabase!!.getTodo().updateTodo(todo)
                }
                finish()
            }
        }
    }

    // Default priority is low (1)
    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        if (checkedId == R.id.medium){
            priority = 2
        }else if (checkedId == R.id.high) {
            priority = 3
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home){
            startActivity(Intent(this, TodoActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

}
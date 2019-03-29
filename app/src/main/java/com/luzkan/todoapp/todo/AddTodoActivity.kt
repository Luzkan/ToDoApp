package com.luzkan.todoapp.todo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.RadioGroup
import android.widget.Toast
import com.luzkan.todoapp.R
import com.luzkan.todoapp.data.local.TodoListDatabase
import com.luzkan.todoapp.data.local.models.Todo
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

        // Moved from the main TodoActivity, retrieving those values here
        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")
        val time = intent.getStringExtra("time")
        val date = intent.getStringExtra("date")

        if (title == null || title == ""){
            addTodo.setOnClickListener{
                // Prevent user from adding silly task w/o title that permanently crashes the app
                if(titleNew == null || titleNew.text.toString() == "") {
                    Toast.makeText(applicationContext,"Set the Title of Todo.",Toast.LENGTH_SHORT).show()
                }else {
                    // The ID (last value of To-do) will be auto incremented in Db
                    val todo = Todo(titleNew.text.toString(), descriptionNew.text.toString(), priority, dateNew.text.toString(), timeNew.text.toString())
                    todoDatabase!!.getTodo().saveTodo(todo)
                    finish()
                }
            }
        }else {
            addTodo.text = getString(R.string.update)
            val tId = intent.getIntExtra("tId", 0)
            // Setting values retrieved from TodoActivity (user is editing stuff)
            titleNew.setText(title)
            descriptionNew.setText(description)
            timeNew.setText(time)
            dateNew.setText(date)

            addTodo.setOnClickListener {
                if(titleNew == null || titleNew.text.toString() == "") {
                    Toast.makeText(applicationContext,"Set the Title of Todo.",Toast.LENGTH_SHORT).show()
                }else if(dateNew.text.toString().length > 5 || timeNew.text.toString().length > 5) {
                    Toast.makeText(applicationContext,"Time or Date should be below 5 characters.",Toast.LENGTH_SHORT).show()
                }else {
                    val todo = Todo(titleNew.text.toString(), descriptionNew.text.toString(), priority, dateNew.text.toString(), timeNew.text.toString(), tId)
                    todoDatabase!!.getTodo().updateTodo(todo)
                    finish()
                }
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
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
import java.sql.Date
import java.text.SimpleDateFormat

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
        val df = SimpleDateFormat("dd.MM.yyyy")

        if (title == null || title == ""){
            addTodo.setOnClickListener{
                // Prevent user from adding silly task w/o title that permanently crashes the app
                if(titleNew == null || titleNew.text.toString() == "") {
                    Toast.makeText(applicationContext,"Set the Title of Todo.",Toast.LENGTH_SHORT).show()
                }else if(timeNew.text.toString().length > 5 || !((dateNew.text.toString().matches("^([0-2][0-9]|(3)[0-1])(\\.)(((0)[0-9])|((1)[0-2]))".toRegex())) || dateNew.text.toString() == "")) {
                    Toast.makeText(applicationContext,"Time should match (hh:mm) and Date  match (Day/Month) format.",Toast.LENGTH_SHORT).show()
                }else {
                    // The ID (last value of To-do) will be auto incremented in Db
                    // @Date: Problem arises when user wants to sort some tasks around the end of December when he scheduled something to January. Obviously easy fix is to create calendar instance in which user can just tap for the date but I wanted to keep it 5 chars quick
                    //        After it's done, the "val df" could be just "getDateInstance()"
                    val todo = Todo(titleNew.text.toString(), descriptionNew.text.toString(), priority, if(dateNew.text.toString() == "") null else Date(df.parse(dateNew.text.toString().plus(".2019")).time), timeNew.text.toString())
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
                }else if(timeNew.text.toString().length > 5 || !((dateNew.text.toString().matches("^([0-2][0-9]|(3)[0-1])(\\.)(((0)[0-9])|((1)[0-2]))".toRegex())) || dateNew.text.toString() == "")) {
                    Toast.makeText(applicationContext,"Time should match (hh:mm) and Date match (Day/Month) format.",Toast.LENGTH_SHORT).show()
                }else {
                    val todo = Todo(titleNew.text.toString(), descriptionNew.text.toString(), priority, if(dateNew.text.toString() == "") null else Date(df.parse(dateNew.text.toString().plus(".2019")).time), timeNew.text.toString(), tId)
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
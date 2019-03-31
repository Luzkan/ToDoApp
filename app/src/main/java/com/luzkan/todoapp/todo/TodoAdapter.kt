package com.luzkan.todoapp.todo

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.luzkan.todoapp.R
import com.luzkan.todoapp.data.local.models.Todo
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.ArrayList

class TodoAdapter(var todoList: List<Todo>? = ArrayList()): RecyclerView.Adapter<TodoAdapter.TodoViewHolder>(){

    private var onTodoItemClickedListener: OnTodoItemClickedListener?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val layout = R.layout.item_todo
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return TodoViewHolder(view, todoList!!)
    }

    override fun getItemCount(): Int {
        return if(todoList!!.isEmpty()) 0 else todoList!!.size
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int){
        holder.view.setOnClickListener{onTodoItemClickedListener!!.onTodoItemClicked(todoList!![position])}
        holder.view.setOnLongClickListener{
            onTodoItemClickedListener!!.onTodoItemLongClicked(todoList!![position])
            true
        }
        holder.onBindViews(position)
    }

    // Fills up the View with stuff from database
    inner class TodoViewHolder(val view: View, private val todoList: List<Todo>): RecyclerView.ViewHolder(view){
        fun onBindViews(position: Int){
            if (itemCount != 0){
                view.findViewById<TextView>(R.id.title).text = todoList[position].title
                view.findViewById<TextView>(R.id.first_letter).text = todoList[position].title.first().toUpperCase().toString()
                view.findViewById<ImageView>(R.id.priority_imgView).setImageResource(getImage(todoList[position].priority))
                view.findViewById<TextView>(R.id.description).text = todoList[position].description
                view.findViewById<TextView>(R.id.date).text = if(todoList[position].date == null) "N/A" else SimpleDateFormat("dd.MM").format(todoList[position].date)
                view.findViewById<TextView>(R.id.time).text = todoList[position].time
            }
        }
        private fun getImage(priority: Int): Int
        = if (priority == 1) R.drawable.low_priority else if(priority == 2) R.drawable.medium_priority else R.drawable.high_priority
    }

    fun setTodoItemClickedListener(onTodoItemClickedListener: OnTodoItemClickedListener){
        this.onTodoItemClickedListener = onTodoItemClickedListener
    }

    interface OnTodoItemClickedListener{
        fun onTodoItemClicked(todo: Todo)
        fun onTodoItemLongClicked(todo: Todo)
    }
}
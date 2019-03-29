package com.luzkan.todoapp.data.local

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.luzkan.todoapp.data.local.models.Todo

@Database(entities = [Todo::class], version = 1, exportSchema = false)
abstract class TodoListDatabase: RoomDatabase(){

    abstract fun getTodo(): TodoInterface

    companion object {
        val databaseName = "tododb"
        var todoListDatabase: TodoListDatabase? = null

        fun getInstance(context: Context): TodoListDatabase?{
            if (todoListDatabase == null){
                todoListDatabase = Room.databaseBuilder(context, TodoListDatabase::class.java, TodoListDatabase.databaseName).allowMainThreadQueries().build()
            }
            return todoListDatabase
        }
    }
}
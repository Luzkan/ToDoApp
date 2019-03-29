package com.luzkan.todoapp.data.local

import android.arch.persistence.room.*
import com.luzkan.todoapp.data.local.models.Todo

@Dao
interface TodoInterface{

    // Explanation
    // http://virtuooza.com/android-update-delete-items-room-database/

    @Query("SELECT*FROM todo ORDER BY tId ASC")
    fun getTodoList(): List<Todo>

    @Query("SELECT*FROM todo WHERE tId=:tid")
    fun getTodoItem(tid: Int): Todo

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun saveTodo(todo: Todo)

    @Update
    fun updateTodo(todo: Todo)

    @Delete
    fun removeTodo(todo: Todo)
}
package com.luzkan.todoapp.data.local

import android.arch.persistence.room.*
import com.luzkan.todoapp.data.local.models.Todo

@Dao
interface TodoInterface{

    // Explanation
    // http://virtuooza.com/android-update-delete-items-room-database/
    // TODO: Fix Bug
    // I wish I could use getTodoListSorted and it's reversed version
    // Although it just doesn't work properly and some people have similar problems around stackoverflow
    // Probably the query is just badly formatted because of the string, but until I sort out why
    // There will be some redundant functions-queries to be deleted later

    @Query("SELECT*FROM todo ORDER BY :sortBy ASC")
    fun getTodoListSorted(sortBy: String): List<Todo>

    @Query("SELECT*FROM todo ORDER BY :sortBy DESC")
    fun getTodoListSortedR(sortBy: String): List<Todo>

    @Query("SELECT*FROM todo WHERE tId=:tid")
    fun getTodoItem(tid: Int): Todo

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun saveTodo(todo: Todo)

    @Update
    fun updateTodo(todo: Todo)

    @Delete
    fun removeTodo(todo: Todo)

    // Here they come

    @Query("SELECT*FROM todo ORDER BY tId ASC")
    fun getTodoList(): List<Todo>

    @Query("SELECT*FROM todo ORDER BY tId DESC")
    fun getTodoListR(): List<Todo>

    @Query("SELECT*FROM todo ORDER BY todo_priority ASC")
    fun getTodoListPrior(): List<Todo>

    @Query("SELECT*FROM todo ORDER BY todo_priority DESC")
    fun getTodoListPriorR(): List<Todo>

    @Query("SELECT*FROM todo ORDER BY todo_date ASC")
    fun getTodoListDate(): List<Todo>

    @Query("SELECT*FROM todo ORDER BY todo_date DESC")
    fun getTodoListDateR(): List<Todo>

    @Query("SELECT*FROM todo ORDER BY todo_title ASC")
    fun getTodoListTitle(): List<Todo>

    @Query("SELECT*FROM todo ORDER BY todo_title DESC")
    fun getTodoListTitleR(): List<Todo>

}
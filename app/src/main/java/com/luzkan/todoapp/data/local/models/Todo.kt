package com.luzkan.todoapp.data.local.models

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "todo")
class Todo(
    @ColumnInfo(name = "todo_title")
    var title:String = "",
    @ColumnInfo(name = "todo_description")
    var description:String = "",
    @ColumnInfo(name = "todo_priority")
    var priority: Int = 1,
    @ColumnInfo(name = "todo_date")
    var date:String = "",
    @ColumnInfo(name = "todo_time")
    var time:String = "",
    @PrimaryKey(autoGenerate = true) var tId: Int = 0)
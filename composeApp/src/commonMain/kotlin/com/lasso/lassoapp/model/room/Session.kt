package com.lasso.lassoapp.model.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Session(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val partnerId: Int,
    val employeeId: Int,
    val partnerName: String,
    val employeeName: String,
    val employeeRole: String
)
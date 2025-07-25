package com.beauty.beautyapp.screens.detail

import androidx.lifecycle.ViewModel
import com.beauty.beautyapp.data.MuseumObject
import com.beauty.beautyapp.data.MuseumRepository
import kotlinx.coroutines.flow.Flow

class DetailViewModel(private val museumRepository: MuseumRepository) : ViewModel() {
    fun getObject(objectId: Int): Flow<MuseumObject?> =
        museumRepository.getObjectById(objectId)
}

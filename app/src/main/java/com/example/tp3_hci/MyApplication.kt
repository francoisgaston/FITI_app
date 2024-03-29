package com.example.tp3_hci

import android.app.Application
import com.example.api_fiti.data.network.api.RetrofitClient
import com.example.tp3_hci.data.network.RoutineRemoteDataSource
import com.example.tp3_hci.data.network.UserRemoteDataSource
import com.example.tp3_hci.data.network.model.CategoryRemoteDataSource
import com.example.tp3_hci.data.repository.CategoryRepository
import com.example.tp3_hci.data.repository.RoutineRepository
import com.example.tp3_hci.data.repository.UserRepository
import com.example.tp3_hci.util.PreferencesManager
import com.example.tp3_hci.util.SessionManager

class MyApplication: Application() {
    private val userRemoteDataSource: UserRemoteDataSource
        get() = UserRemoteDataSource(sessionManager,RetrofitClient.getApiUserService(this))
    private val routineRemoteDataSource: RoutineRemoteDataSource
        get() = RoutineRemoteDataSource(RetrofitClient.getApiRoutineService(this))
    private val categoryRemoteDataSource: CategoryRemoteDataSource
        get() = CategoryRemoteDataSource(RetrofitClient.getApiCategoryService(this))
    val sessionManager: SessionManager
        get() = SessionManager(this)
    val preferencesManager: PreferencesManager
        get() = PreferencesManager(this)
    val userRepository: UserRepository
        get() = UserRepository(userRemoteDataSource)
    private val categoryRepository: CategoryRepository
        get() = CategoryRepository(categoryRemoteDataSource)
    val routineRepository: RoutineRepository
        get() = RoutineRepository(routineRemoteDataSource,userRepository,categoryRepository)
}
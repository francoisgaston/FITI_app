package com.example.tp3_hci.data.repository

import com.example.api_fiti.data.network.model.NetworkUser
import com.example.tp3_hci.data.model.User
import com.example.tp3_hci.data.network.UserRemoteDataSource
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class UserRepository(
    private val remoteDataSource: UserRemoteDataSource
) {
    private val currentUserMutex = Mutex()

    private var currentUser: User? = null

    suspend fun login(username: String, password: String){
        remoteDataSource.login(username,password)
    }
    suspend fun logout(){
        remoteDataSource.logout()
    }
    suspend fun getCurrentUser(refresh: Boolean):User?{
        if(refresh||currentUser!=null) {
            val result = remoteDataSource.getCurrentUser()
            currentUserMutex.withLock {
                this.currentUser = result.asModel()
            }
        }
        return currentUserMutex.withLock { this.currentUser }
    }
}
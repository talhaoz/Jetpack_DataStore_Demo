package com.talhaoz.testzone

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreManager(private val context: Context) {

    suspend fun addUserToPreferences(user: User) {
        context.userDataStore.edit { preferences ->
            preferences[USER_NAME] = user.name
            preferences[USER_AGE] = user.age
        }
    }

    suspend fun addUserToProto(user: User) {
        context.protoUserDataStore.updateData { preferences ->
            preferences.toBuilder()
                .setName(user.name)
                .setAge(user.age)
                .build()
        }
    }

    val getUserFromPreferences : Flow<User>
        get() = context.userDataStore.data.map { preferences ->
            User(
                name = preferences[USER_NAME] ?: "",
                age = preferences[USER_AGE] ?: 0
            )
        }

    val getUserFromProto : Flow<User>
        get() = context.protoUserDataStore.data.map { preferences ->
            User(
                name = (preferences.name as String),
                age = preferences.age.toInt()
            )
        }

    companion object {
        private const val DATASTORE_NAME = "user_preferences"
        private val USER_NAME = stringPreferencesKey("userName")
        private val USER_AGE = intPreferencesKey("userAge")

        // Proto DataStore
        private const val PROTO_DATA_STORE_FILE_NAME = "user_prefs.pb"
        private const val SORT_ORDER_KEY = "sort_order"

        private val Context.userDataStore by preferencesDataStore(
            name = DATASTORE_NAME
        )

        private val Context.protoUserDataStore: DataStore<UserPreference> by dataStore(
            fileName = PROTO_DATA_STORE_FILE_NAME,
            serializer = UserSerializer
        )
    }
}
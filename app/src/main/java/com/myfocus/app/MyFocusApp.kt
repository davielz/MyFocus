package com.myfocus.app

import android.app.Application
import com.myfocus.app.data.local.AppDatabase
import com.myfocus.app.data.local.DataStoreManager
import com.myfocus.app.data.repository.FocusRepository

/**
 * Kelas Application utama untuk inisialisasi dependensi lokal MyFocus.
 * Tanpa inisialisasi SDK pihak ketiga/iklan/analitik (Aturan #1).
 */
class MyFocusApp : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var dataStoreManager: DataStoreManager
        private set

    lateinit var repository: FocusRepository
        private set

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getInstance(this)
        dataStoreManager = DataStoreManager(this)
        repository = FocusRepository(database, dataStoreManager)
    }
}


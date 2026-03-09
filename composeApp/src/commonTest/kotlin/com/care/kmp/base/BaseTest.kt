package com.care.kmp.base

import com.care.kmp.repository.FakeTodoRepository
import com.care.kmp.repository.FakeUserRepository
import com.care.kmp.di.fakeUserRepo
import com.care.kmp.di.fakeTodoRepo
import com.care.kmp.di.testAppModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

@OptIn(ExperimentalCoroutinesApi::class)
abstract class BaseTest {

    protected val todoRepo: FakeTodoRepository get() = fakeTodoRepo
    protected val userRepo: FakeUserRepository get() = fakeUserRepo

    @BeforeTest
    fun baseSetup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        startKoin { modules(testAppModule) }
    }

    @AfterTest
    fun baseTearDown() {
        todoRepo.reset()
        userRepo.reset()
        stopKoin()
        Dispatchers.resetMain()
    }
}
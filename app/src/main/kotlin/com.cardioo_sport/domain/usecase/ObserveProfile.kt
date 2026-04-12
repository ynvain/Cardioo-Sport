package com.cardioo_sport.domain.usecase

import com.cardioo_sport.domain.model.UserProfile
import com.cardioo_sport.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveProfile @Inject constructor(
    private val repo: UserRepository,
) {
    operator fun invoke(): Flow<UserProfile?> = repo.observeProfile()
}


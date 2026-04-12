package com.cardioo_sport.domain.usecase

import com.cardioo_sport.domain.model.UserProfile
import com.cardioo_sport.domain.repository.UserRepository
import javax.inject.Inject

class UpsertProfile @Inject constructor(
    private val repo: UserRepository,
) {
    suspend operator fun invoke(profile: UserProfile) = repo.upsertProfile(profile)
}


package com.github.iojjj.survey.networking.retrofit

import com.github.iojjj.survey.networking.retrofit.internal.RetrofitModule
import dagger.Module

@Module(includes = [RetrofitModule::class])
interface NetworkingModule
package dev.prince.prodspec.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.prince.prodspec.database.ProductDao
import dev.prince.prodspec.database.ProductDatabase
import dev.prince.prodspec.network.ApiService
import dev.prince.prodspec.util.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = Retrofit
        .Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit) = retrofit.create(ApiService::class.java)

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context,
        ProductDatabase::class.java,
        "product_database"
    ).build()

    @Provides
    @Singleton
    fun provideProductDao(productDatabase: ProductDatabase): ProductDao {
        return productDatabase.productDao()
    }

}
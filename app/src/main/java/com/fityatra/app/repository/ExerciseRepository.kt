package com.fityatra.app.repository

import com.fityatra.app.data.dao.CategoryDao
import com.fityatra.app.data.dao.ExerciseDao
import com.fityatra.app.data.entities.Category
import com.fityatra.app.data.entities.Exercise
import kotlinx.coroutines.flow.Flow

class ExerciseRepository(
    private val exerciseDao: ExerciseDao,
    private val categoryDao: CategoryDao
) {
    fun getAllExercises(): Flow<List<Exercise>> = exerciseDao.getAllExercises()
    
    fun getExercisesByCategory(categoryId: Long): Flow<List<Exercise>> = 
        exerciseDao.getExercisesByCategory(categoryId)
    
    fun getAllCategories(): Flow<List<Category>> = categoryDao.getAllCategories()
    
    suspend fun getExerciseById(id: Long): Exercise? = exerciseDao.getExerciseById(id)

    suspend fun getExerciseByName(name: String): Exercise? = exerciseDao.getExerciseByName(name)
    
    suspend fun getCategoryById(id: Long): Category? = categoryDao.getCategoryById(id)
    
    suspend fun insertExercise(exercise: Exercise): Long = exerciseDao.insertExercise(exercise)
    
    suspend fun insertCategory(category: Category): Long = categoryDao.insertCategory(category)
    
    suspend fun updateExercise(exercise: Exercise) = exerciseDao.updateExercise(exercise)
    
    suspend fun deleteExercise(exercise: Exercise) = exerciseDao.deleteExercise(exercise)
    
    fun getBuiltinExercises(): Flow<List<Exercise>> = exerciseDao.getBuiltinExercises()
    
    fun getUserExercises(): Flow<List<Exercise>> = exerciseDao.getUserExercises()
}

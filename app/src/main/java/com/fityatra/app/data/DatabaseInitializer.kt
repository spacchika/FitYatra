package com.fityatra.app.data

import com.fityatra.app.data.dao.CategoryDao
import com.fityatra.app.data.dao.ExerciseDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DatabaseInitializer(
    private val categoryDao: CategoryDao,
    private val exerciseDao: ExerciseDao
) {
    
    fun initializeDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            // Check if data already exists
            val existingCategories = categoryDao.getAllCategories()
            
            // Insert categories if they don't exist
            PrefilledData.categories.forEach { category ->
                try {
                    categoryDao.insertCategory(category)
                } catch (e: Exception) {
                    // Category might already exist, continue
                }
            }
            
            // Insert exercises if they don't exist
            PrefilledData.exercises.forEach { exercise ->
                try {
                    exerciseDao.insertExercise(exercise)
                } catch (e: Exception) {
                    // Exercise might already exist, continue
                }
            }
        }
    }
}

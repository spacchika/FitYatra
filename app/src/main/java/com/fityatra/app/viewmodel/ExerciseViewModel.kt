package com.fityatra.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fityatra.app.data.entities.Category
import com.fityatra.app.data.entities.Exercise
import com.fityatra.app.repository.ExerciseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExerciseViewModel(
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {
    
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()
    
    private val _exercises = MutableStateFlow<List<Exercise>>(emptyList())
    val exercises: StateFlow<List<Exercise>> = _exercises.asStateFlow()
    
    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory: StateFlow<Category?> = _selectedCategory.asStateFlow()
    
    init {
        loadCategories()
        loadAllExercises()
    }
    
    private fun loadCategories() {
        viewModelScope.launch {
            exerciseRepository.getAllCategories().collect { categories ->
                _categories.value = categories
            }
        }
    }
    
    private fun loadAllExercises() {
        viewModelScope.launch {
            exerciseRepository.getAllExercises().collect { exercises ->
                _exercises.value = exercises
            }
        }
    }
    
    fun selectCategory(category: Category) {
        _selectedCategory.value = category
        viewModelScope.launch {
            exerciseRepository.getExercisesByCategory(category.id).collect { exercises ->
                _exercises.value = exercises
            }
        }
    }
    
    fun loadAllExercisesForCategory() {
        loadAllExercises()
        _selectedCategory.value = null
    }
    
    fun addCustomExercise(name: String, categoryId: Long, description: String) {
        viewModelScope.launch {
            val exercise = Exercise(
                name = name,
                categoryId = categoryId,
                description = description,
                isBuiltin = false
            )
            exerciseRepository.insertExercise(exercise)
        }
    }
    
    fun addCustomCategory(name: String) {
        viewModelScope.launch {
            val category = Category(name = name)
            exerciseRepository.insertCategory(category)
        }
    }
}

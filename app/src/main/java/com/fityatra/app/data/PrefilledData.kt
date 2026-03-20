package com.fityatra.app.data

import com.fityatra.app.data.entities.Category
import com.fityatra.app.data.entities.Exercise

object PrefilledData {
    
    val categories = listOf(
        Category(1, "Chest"),
        Category(2, "Back"),
        Category(3, "Shoulders"),
        Category(4, "Biceps"),
        Category(5, "Triceps"),
        Category(6, "Legs"),
        Category(7, "Core"),
        Category(8, "Cardio")
    )
    
    val exercises = listOf(
        // Chest
        Exercise(1, "Bench Press", 1, "Barbell bench press", true),
        Exercise(2, "Incline Bench Press", 1, "Incline barbell bench press", true),
        Exercise(3, "Dumbbell Press", 1, "Flat dumbbell press", true),
        Exercise(4, "Incline Dumbbell Press", 1, "Incline dumbbell press", true),
        Exercise(5, "Push-ups", 1, "Standard push-ups", true),
        Exercise(6, "Dips", 1, "Parallel bar dips", true),
        Exercise(7, "Chest Fly", 1, "Dumbbell chest fly", true),
        
        // Back
        Exercise(8, "Pull-ups", 2, "Standard pull-ups", true),
        Exercise(9, "Chin-ups", 2, "Underhand chin-ups", true),
        Exercise(10, "Lat Pulldown", 2, "Cable lat pulldown", true),
        Exercise(11, "Barbell Row", 2, "Bent-over barbell row", true),
        Exercise(12, "Dumbbell Row", 2, "Single-arm dumbbell row", true),
        Exercise(13, "T-Bar Row", 2, "T-bar row", true),
        Exercise(14, "Deadlift", 2, "Conventional deadlift", true),
        Exercise(15, "Romanian Deadlift", 2, "Romanian deadlift", true),
        
        // Shoulders
        Exercise(16, "Overhead Press", 3, "Standing barbell overhead press", true),
        Exercise(17, "Dumbbell Shoulder Press", 3, "Seated dumbbell shoulder press", true),
        Exercise(18, "Lateral Raises", 3, "Dumbbell lateral raises", true),
        Exercise(19, "Front Raises", 3, "Dumbbell front raises", true),
        Exercise(20, "Rear Delt Fly", 3, "Rear deltoid fly", true),
        Exercise(21, "Upright Row", 3, "Barbell upright row", true),
        Exercise(22, "Arnold Press", 3, "Arnold dumbbell press", true),
        
        // Biceps
        Exercise(23, "Barbell Curl", 4, "Standing barbell curl", true),
        Exercise(24, "Dumbbell Curl", 4, "Alternating dumbbell curl", true),
        Exercise(25, "Hammer Curl", 4, "Dumbbell hammer curl", true),
        Exercise(26, "Preacher Curl", 4, "Preacher bench curl", true),
        Exercise(27, "Cable Curl", 4, "Cable bicep curl", true),
        Exercise(28, "Concentration Curl", 4, "Seated concentration curl", true),
        
        // Triceps
        Exercise(29, "Close-Grip Bench Press", 5, "Close-grip barbell bench press", true),
        Exercise(30, "Tricep Dips", 5, "Tricep dips", true),
        Exercise(31, "Overhead Tricep Extension", 5, "Dumbbell overhead tricep extension", true),
        Exercise(32, "Tricep Pushdown", 5, "Cable tricep pushdown", true),
        Exercise(33, "Diamond Push-ups", 5, "Diamond push-ups", true),
        Exercise(34, "Skull Crushers", 5, "Lying tricep extension", true),
        
        // Legs
        Exercise(35, "Squat", 6, "Barbell back squat", true),
        Exercise(36, "Front Squat", 6, "Barbell front squat", true),
        Exercise(37, "Leg Press", 6, "Leg press machine", true),
        Exercise(38, "Lunges", 6, "Walking lunges", true),
        Exercise(39, "Bulgarian Split Squat", 6, "Bulgarian split squat", true),
        Exercise(40, "Leg Curl", 6, "Hamstring curl", true),
        Exercise(41, "Leg Extension", 6, "Quadriceps extension", true),
        Exercise(42, "Calf Raises", 6, "Standing calf raises", true),
        Exercise(43, "Hip Thrust", 6, "Barbell hip thrust", true),
        
        // Core
        Exercise(44, "Plank", 7, "Standard plank", true),
        Exercise(45, "Crunches", 7, "Abdominal crunches", true),
        Exercise(46, "Russian Twists", 7, "Russian twists", true),
        Exercise(47, "Leg Raises", 7, "Lying leg raises", true),
        Exercise(48, "Mountain Climbers", 7, "Mountain climbers", true),
        Exercise(49, "Dead Bug", 7, "Dead bug exercise", true),
        Exercise(50, "Bicycle Crunches", 7, "Bicycle crunches", true),
        
        // Cardio
        Exercise(51, "Running", 8, "Outdoor or treadmill running", true),
        Exercise(52, "Cycling", 8, "Stationary or outdoor cycling", true),
        Exercise(53, "Rowing", 8, "Rowing machine", true),
        Exercise(54, "Jump Rope", 8, "Jump rope cardio", true),
        Exercise(55, "Burpees", 8, "Full-body burpees", true)
    )
}

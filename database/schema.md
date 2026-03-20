# FitYatra Database Schema (Draft)

## Tables

### 1. Category
- id (PK)
- name (e.g., Biceps, Triceps, Back, etc.)

### 2. Exercise
- id (PK)
- name
- category_id (FK -> Category)
- description
- is_builtin (bool, true for pre-filled, false for user-added)

### 3. WorkoutPlan
- id (PK)
- name
- description
- days_per_week (int)
- is_active (bool)

### 4. WorkoutPlanExercise
- id (PK)
- plan_id (FK -> WorkoutPlan)
- exercise_id (FK -> Exercise)
- day_of_week (int, 1=Monday)
- order_in_day (int)

### 5. WorkoutSession
- id (PK)
- plan_id (FK -> WorkoutPlan)
- date
- is_deload (bool)

### 6. WorkoutSet
- id (PK)
- session_id (FK -> WorkoutSession)
- exercise_id (FK -> Exercise)
- set_number
- reps
- weight
- duration_seconds
- rest_seconds

### 7. Backup
- id (PK)
- backup_time
- location (local, Google Drive, OneDrive)
- file_path

### 8. UserSettings
- id (PK)
- deload_frequency_weeks (int)
- deload_weight_drop_percent (int)
- rest_timer_default (int)
- backup_auto (bool)
- backup_schedule (cron/interval)

---

This schema supports pre-filled and user-added workouts, categorized exercises, custom plans, session tracking, deload logic, and backup options.

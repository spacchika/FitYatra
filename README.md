# FitYatra

FitYatra is an AI-powered workout companion for Android. It pairs a conversational Claude-based AI coach with structured workout planning, session logging, progress tracking, and optional Health Connect integration — all stored locally on device.

---

## Features

### AI Coach
- Conversational interface powered by the Claude API (claude-haiku-4-5)
- Context-aware responses: knows your active plan, today's exercises, experience level, and health conditions
- Optionally reads Health Connect wellness data (HRV, resting HR, sleep) to adjust recommendations
- Suggests workout adjustments for poor recovery days
- Answers questions about form, nutrition, and programming

### AI-Powered Onboarding
- 6-step profile wizard: body metrics, fitness goal, experience, health conditions, preferences, equipment
- AI generates a personalised workout plan from your profile
- **Plan preview step**: the suggested plan is shown for review (exercises grouped by day with sets/reps/weight/rest) before anything is saved
- Accept the plan, or ask the AI to generate a new one
- Profile and plan are only saved after explicit acceptance

### Workout Plans
- Create, edit, and delete plans
- Set any plan as active
- AI-assisted exercise selection during plan creation
- Plans broken down by day of week (Mon–Sun) with warmup / main / cooldown categorisation
- Per-exercise: sets, reps, target weight, rest period, notes

### Workout Session Logging
- Start a session from any plan day
- Log sets with reps, weight, and duration
- Built-in rest timer with countdown
- Deload week support: automatic volume/intensity reduction every 6–12 weeks (configurable)
- Deload frequency and weight-drop percentage adjustable in Settings

### Exercise Library
- 40+ built-in exercises categorised by muscle group (Chest, Back, Shoulders, Biceps, Triceps, Legs, Core, Cardio)
- Add custom exercises and custom categories
- Filter by category

### Progress & Stats
- Total workout count, current streak, and average session duration
- Per-exercise progress tracking: current vs. previous weight and reps
- Trend indicators (increasing / stable / decreasing)
- Automatic weight suggestions based on recent performance

### Calendar
- Month view with colour-coded training and deload days
- Navigate between months
- Mark deload weeks manually

### Health Connect Integration
- Reads HRV, resting heart rate, VO2 Max, and sleep data (optional)
- Wellness banner in the AI Coach screen
- AI coach references wellness data when advising on intensity and recovery

### Settings & Data
- Claude API key configuration (stored on-device only)
- Export and import local data (JSON)
- Deload frequency and weight-drop percentage
- Default rest timer duration

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 1.9 |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM — ViewModel, StateFlow, Repository |
| Database | Room 2.5 (SQLite) |
| Navigation | Navigation Compose 2.7 |
| AI | Anthropic Claude API via OkHttp 4.12 |
| Health | Android Health Connect 1.1.0-alpha07 |
| Min SDK | 26 (Android 8.0) |
| Target SDK | 34 (Android 14) |

---

## Getting Started

1. **Open in Android Studio** (Hedgehog or newer recommended).
2. **Build and run** on a physical device or emulator (API 26+).
3. Complete the **onboarding wizard** to create your profile and first AI-generated plan.
4. In **Settings**, enter your **Claude API key** (get one at [console.anthropic.com](https://console.anthropic.com)) to enable the AI Coach and plan generation.
5. Optionally connect **Health Connect** to allow the AI Coach to reference your wellness data.

> The app works without an API key for manual workout tracking, but AI coaching and plan generation require a valid Anthropic API key.

---

## Project Structure

```
app/src/main/java/com/fityatra/app/
├── ai/             # Claude API client, prompt builder, plan parser
├── data/           # Room database, DAOs, entities, migrations
├── health/         # Health Connect integration
├── repository/     # Data access layer (AI coach, workouts, exercises, profile)
├── ui/screens/     # Compose screens (onboarding, AI coach, plans, sessions, etc.)
├── viewmodel/      # ViewModels for each screen
└── MainActivity.kt # Navigation host
```

---

## License

MIT

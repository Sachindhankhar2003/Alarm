# ⏰ WakeUp Math - Advanced Math Alarm App

**WakeUp Math** is a modern, feature-packed Android alarm application built with **Jetpack Compose**, **Kotlin**, and **Room DB**. Designed for heavy sleepers, students, and productivity enthusiasts, it forces your brain to wake up by requiring you to solve customizable math problems to dismiss the alarm.

---

## 🌟 Key Features

- 🧮 **5 Math Challenge Categories**: Solve problems in **Trigonometry**, **Algebra**, **Calculus**, **Logarithms**, or **Mixed**.
- 🔊 **Synthesized PCM Audio Engine**: Select from 5 distinct alarm sounds (*Harsh Sawtooth*, *Siren Pulse*, *Digital Beep*, *Sine Chime*, *Radar Sweep*).
- 📈 **Gradual Volume Crescendo**: Alarm audio gently fades in from 15% to 100% volume over 30 seconds to avoid sudden heart-rate spikes.
- 📱 **Physical Phone Shake Mission**: Uses the device accelerometer (`Sensor.TYPE_ACCELEROMETER`) to require physical movement before unlocking the math challenge.
- 🛡️ **Anti-Cheating 3-Question Penalty**: Repeated incorrect attempts trigger a 3-consecutive question penalty mode.
- 🧠 **Speed Math Practice Gym**: Train your math solving speed anytime offline without waiting for an alarm.
- 📊 **Brain Stats & Streak Tracker**: Tracks solve speed (seconds), total alarms solved, and unlocks achievement badges (*Math Genius*, *Early Bird*, *Beginner*).
- 🌙 **Sleep Cycle Recommendation**: Recommends optimal 90-minute sleep cycle bedtime targets.

---

## 🛠️ Tech Stack

- **Language**: Kotlin 2.0
- **UI Framework**: Jetpack Compose & Material 3 (Custom Dark Theme & Outfit Typography)
- **Database**: Room Database with Coroutines & StateFlow
- **Architecture**: MVVM with Repository pattern
- **Audio Engine**: Android `AudioTrack` PCM real-time synthesizer
- **Background Service**: Foreground Service with High Priority Notifications & Lockscreen Display (`setShowWhenLocked`)

---

## 🚀 How to Build & Run

1. **Clone the repository**:
   ```bash
   git clone https://github.com/Sachindhankhar2003/Alarm.git
   cd Alarm
   ```

2. **Build the Debug APK**:
   ```bash
   ./gradlew assembleDebug
   ```

3. **Install on your Android Device**:
   The output APK will be generated at `app/build/outputs/apk/debug/app-debug.apk`.

---

## 📄 License

Distributed under the MIT License. See `LICENSE` for more details.

# 🌟 Awareness Expansion
🚀 Awareness Expansion is a Java-based Android app crafted to elevate your understanding of wellness, vital body mechanisms, quantum body structure, and mind mastery. Dive into a world of scientific insights through curated audio content, all wrapped in a modern, user-friendly interface. Built with Java, Volley, and a PHP backend, this app combines educational audio with seamless email notifications and smooth animations.

## ✨ Key Features

🎧 Educational Audio Content: Listen to audio files on wellness, body mechanisms, quantum structures, phenomena, and mind observation.
🖼️ Modern UI: Designed with Material Design, featuring icons, animations (fade & slide), and a vibrant theme (#6A1B9A).
✅ Form Validation: Ensures accurate user input for feedback or inquiries.
📧 Email Notifications: Sends messages via a PHP backend, with optimized delay handling.
🛠️ Error Management: Handles network delays (resolved 98-second delays with 5-second Timeout) and displays success dialogs.
🌈 Smooth Animations: Slide-in and fade-out effects for an engaging experience.

## 🎯 Mission
Awareness Expansion empowers users to deepen their self-awareness and connection to life. From understanding the body’s vital mechanisms (e.g., circulation, respiration) to exploring quantum structures and mastering the mind, this app is a gateway to personal growth and scientific self-discovery.

## 🚀 Getting Started
🛠️ Prerequisites

Android Studio (latest version) 🖥️
Android SDK 📱
Internet connection for API and audio streaming 🌐

## 📦 Installation

Clone the repository:git clone https://github.com/k-aghakhani/awareness.git


Open the project in Android Studio.
Add these dependencies in build.gradle:implementation 'com.google.android.material:material:1.11.0'
implementation 'com.android.volley:volley:1.2.1'
implementation 'androidx.media3:media3-exoplayer:1.3.1' // For audio playback


Configure the API endpoint in ContactFormActivity.java 
Add audio files to the res/raw directory (e.g., awareness_audio1.mp3).
Build and run the app on an emulator or device.


## 🎧 Usage

📚 Explore the audio library to dive into topics like wellness, quantum body structure, and mind mastery.
✍️ Use the contact form to submit questions or feedback, triggering an email notification.
🌟 Enjoy the smooth animations and responsive UI during interactions.


## 🔊 Audio Content Setup

Place audio files (MP3/WAV) in the res/raw folder.
Reference them in the app (e.g., R.raw.awareness_audio1) for playback with ExoPlayer.
Ensure file compatibility with Android’s media playback requirements.


## 🌐 Backend Setup
The app uses a PHP backend for email notifications:

Upload send_email.php to your server.
Enable PHP and mail functionality on your server.
Update the $to variable in send_email.php with your email addresses.


## 🤝 Contributing
We’d love your contributions! Here’s how to join:

🍴 Fork the repository.
🌿 Create a new branch: git checkout -b feature-name.
💻 Make your changes and commit: git commit -m "Add new feature".
📤 Push to the branch: git push origin feature-name.
📜 Open a pull request.

Please adhere to the project’s coding style and include tests where applicable.#OpenSource #Contribute #AndroidDev

## 🐞 Issues
Found a bug or have a suggestion? Open an issue and let us know!#BugReport #FeatureRequest

## 📧 Contact
Have questions or ideas? Reach out:  

✉️ Email: kiarash1988@gmail.com  
🖥️ GitHub: @k-aghakhani

#ContactUs #Feedback

## 📜 License
This project is licensed under the MIT License - see the LICENSE.md file for details.#MITLicense

## 🙏 Acknowledgments

A huge thanks to the Android, Volley, and Media3 communities for their amazing tools! 🙌
Inspired by the journey of scientific self-awareness and personal growth. 🌱

#ThankYou #SelfAwareness

## 🌟 Happy Exploring! 
🌟#AwarenessExpansion #Wellness #Mindfulness #QuantumBody

Made with ❤️ and ☕

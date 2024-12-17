# Cosmic

![play_store_feature_graphic](https://github.com/user-attachments/assets/dbba0a69-383b-4e44-921f-35e418a7fe2e)


Cosmic is an open-source Android client for Bluesky, built with modern Android development practices and a focus on user experience.

## Features

- Native Android implementation following Material Design principles
- Real-time feed updates and notifications
- Media support for images and videos
- Theme customization
- Offline support
- Privacy-focused with local data storage

## Tech Stack

- **Language**: Kotlin
- **Architecture**: Clean Architecture with MVVM
- **UI**: Jetpack Compose
- **Dependency Injection**: Koin
- **Networking**: Ktor
- **Local Storage**: 
  - DataStore Preferences
  - Room Database
- **Background Processing**: WorkManager
- **Media Handling**: Media3
- **Testing**: 
  - JUnit
  - Turbine for Flow testing
  - MockK for mocking

## Getting Started

### Prerequisites

- Android Studio Arctic Fox or newer
- JDK 17 or newer
- Android SDK with minimum API level 31

### Building the Project

1. Clone the repository:
```bash
git clone https://github.com/yourusername/cosmic.git
```

2. Open the project in Android Studio

3. Sync project with Gradle files

4. Build the project:
```bash
./gradlew build
```

### Running Tests

```bash
./gradlew test
```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request. For major changes, please open an issue first to discuss what you would like to change.

### Development Workflow

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Built on the [AT Protocol](https://atproto.com/)
- Thanks to the Bluesky team for their work on the protocol
- Thanks to all contributors who have helped shape this project

## Contact

Project Link: [https://github.com/jlroberts1/Cosmic](https://github.com/jlroberts1/Cosmic)

## Disclaimer

This is an unofficial client and is not affiliated with Bluesky or the AT Protocol team.

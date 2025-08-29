# AI Web Browser

A simple Android web browser with AI-powered translation capabilities using OpenRouter and Mistral APIs.

## Features

- **Web Browsing**: Full-featured web browser with navigation controls
- **AI Translation**: Select text on any webpage and translate using AI models
- **Model Selection**: Choose from OpenRouter and Mistral AI models
- **Simple UI**: Clean, easy-to-use interface
- **API Integration**: Support for both OpenRouter and Mistral APIs

## Setup Instructions

### 1. Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 24 or higher
- OpenRouter API key (optional)
- Mistral API key (optional)

### 2. Installation
1. Open the project in Android Studio
2. Sync the project with Gradle files
3. Build and run the app on your device or emulator

### 3. Configuration
1. Open the app and tap the settings icon (gear) in the toolbar
2. Enter your API keys:
   - **OpenRouter API Key**: Get from [OpenRouter](https://openrouter.ai/)
   - **Mistral API Key**: Get from [Mistral AI](https://mistral.ai/)
3. Tap "Refresh Models" to load available AI models
4. Select your preferred model for translation
5. Tap "Save" to store your settings

## How to Use

### Basic Browsing
- Enter URLs or search terms in the address bar
- Use back/forward buttons for navigation
- Tap refresh to reload pages

### AI Translation
1. Browse to any webpage
2. Select text you want to translate by highlighting it
3. A translation panel will appear at the bottom
4. The selected text will be automatically translated to English
5. Tap "Cancel" to close the translation panel

### Model Management
- Tap the settings icon to access model selection
- Enter your API keys for OpenRouter and/or Mistral
- Refresh the models list to see available options
- Select your preferred model for translations

## API Keys

### OpenRouter
1. Visit [OpenRouter](https://openrouter.ai/)
2. Sign up for an account
3. Generate an API key from your dashboard
4. Enter the key in the app settings

### Mistral AI
1. Visit [Mistral AI](https://mistral.ai/)
2. Create an account
3. Generate an API key
4. Enter the key in the app settings

## Supported Models

The app automatically fetches available models from:
- **OpenRouter**: Various models including GPT, Claude, and others
- **Mistral**: Mistral's language models

## Privacy & Security

- API keys are stored locally on your device
- No browsing data is sent to external servers except for translation requests
- Translation requests only include the selected text
- API keys are excluded from device backups for security

## Troubleshooting

### No Models Available
- Check your internet connection
- Verify your API keys are correct
- Ensure you have sufficient API credits

### Translation Not Working
- Make sure you've selected a model
- Check that your API key is valid
- Verify the selected text is not too long

### App Crashes
- Clear app data and re-enter API keys
- Check Android version compatibility (minimum API 24)

## Technical Details

- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **Architecture**: Native Android with Retrofit for API calls
- **UI Framework**: Material Design Components

## License

This project is open source and available under the MIT License.
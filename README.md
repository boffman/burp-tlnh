# Top Level Navigation Highlighter

A Burp Suite extension that automatically highlights top-level navigation requests in the Proxy history, making it easier to identify and track primary page navigations during security testing.

## What It Does

This extension monitors HTTP requests passing through Burp's Proxy and highlights those that represent top-level navigation events. It does this by checking for the `Sec-Fetch-Mode` header with values of `navigate` or `nested-navigate`, which modern browsers send to indicate primary navigation requests (as opposed to AJAX calls, images, or other resource loads).

### Features

- **Automatic Highlighting**: Requests are highlighted in real-time as they pass through Burp Proxy
- **Customizable Colors**: Choose your preferred highlight color from the extension's settings tab
- **Persistent Settings**: Your color preference is saved between Burp sessions
- **Lightweight**: Minimal performance impact - only processes proxy requests

## Installation

### From GitHub Releases

1. Download the latest `top-level-navigation-highlighter-X.X.X.jar` file from the [Releases](https://github.com/boffman/burp-tlnh/releases) page
2. Open Burp Suite
3. Go to the **Extensions** tab
4. Click **Add** under "Installed"
5. Select **Extension file** and browse to the downloaded JAR file
6. Click **Next**
7. The extension should load successfully - look for "TLNH loaded" in the extension output

### Manual Build (see below)

Follow the build instructions below to create the JAR file yourself.

## Usage

1. Once installed, the extension automatically starts monitoring proxy traffic
2. Navigate to the **TLNH** tab in Burp Suite to configure the highlight color
3. Select your preferred color from the dropdown and click **Save**
4. Check the Proxy > HTTP history - requests with `Sec-Fetch-Mode: navigate` or `nested-navigate` will be highlighted in your chosen color
5. These highlighted requests represent top-level navigations (page loads) rather than background resource requests

## Building from Source

### Requirements

- **Java Development Kit (JDK) 17** or higher
- **Apache Maven 3.6+**
- Internet connection (for downloading dependencies)

### Build Steps

1. Clone the repository:
   ```bash
   git clone https://github.com/boffman/burp-tlnh.git
   cd burp-tlnh
   ```

2. Build with Maven:
   ```bash
   mvn clean package
   ```

3. The compiled JAR file will be created in the `target/` directory:
   ```
   target/top-level-navigation-highlighter-1.1.0-SNAPSHOT.jar
   ```

4. Load this JAR file into Burp Suite using the installation steps above

### Build Configuration

The extension is built using:
- **Burp Montoya API 2025.8** (provided scope - not bundled in JAR)
- **Maven Compiler Plugin** targeting Java 17

## Troubleshooting

### Extension doesn't load
- Ensure you're using Burp Suite with Montoya API support (2023.1+)
- Check the extension output/errors tab for error messages
- Verify you're using Java 17 or higher

### Requests aren't being highlighted
- Ensure the browser is sending `Sec-Fetch-Mode` headers (modern browsers like Chrome, Firefox, Edge should send these)
- Verify the requests are going through the Proxy (check HTTP history, compare with Logger)
- Older browsers or certain request types may not include these headers

### Color changes don't appear
- Make sure to click the **Save** button after selecting a new color
- Changes apply to new requests; existing history items retain their original highlighting

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Please feel free to submit issues or pull requests.

## Author

boffman

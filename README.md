# Chatcord Client

**JavaFX Frontend for Chatcord Desktop Chat Application**

This is the **client-side** desktop application for Chatcord, built using JavaFX and styled for a modern messaging experience. It connects to the Spring Boot backend via WebSocket and REST, offering a secure and responsive chat UI.

---

## 🎯 Key Features

- **Modern Chat Interface**
  - Clickable contact list
  - Real-time chat updates
  - Chat switching UI transitions
  - Loading async while fetching data with animations.

- **Event-Driven Architecture**
  - JavaFX event publishing & listening (FXML controller communication)
  - NavigationBar, ChatController, and ContactController work seamlessly via event flow
  - Separation of concerns for different FXML and controllers.

- **Secure Authentication Handling**
  - Login via REST API (JWT-based)
  - Stores access and refresh tokens
  - Sends JWT in headers during WebSocket handshake
  - Refresh Tokens are saved in the AppData folder with device and UUID IDs.
  - Email verification otp.
  - Resened otp feature with timer synced with backend.

- **Multi-Device Support**
  - Unique client identifier
  - OTP device verification flow
  - Device ID saved in Windows `AppData` directory

- **Responsive UI Design**
  - FXML layouts loaded via Spring context
  - Hover/select/focus styles for components
  - Custom-designed controls (buttons, switchers, fields)
  - Styling using CSS & using better approaches for UI/UX like switchers.

---

## 🧰 Technologies Used

- Java 17+
- JavaFX 19+
- Spring Boot (for DI and service access)
- WebClient (for RESTful APIs)
- WebSocket (via Spring Messaging)
- Maven
- My custom JavaFx node of OnyxFx Repository (Custom Switcher for JavaFX)

---

## 🗂️ Project Highlights

- Modular component controllers (Chat, Contact, Profile, Navigation)
- Custom JavaFX styles
- Integration with Spring's `@Component` and FXML Loader
- Runnable multithreaded client socket handling for future scalability
- Device information securely stored in OS-level app data directories
- Custom JavaFX component named OFxSwitcher (OnyxFx)
- You can check my repository for OnyxFx here: https://github.com/Mu7ammedDev159/OnyxFx-Graphics
- Using UDP Connections for Community open chats since UDP connections are connectionless.

---

## 🖥️ How to Run

1. Clone the frontend repo:
```bash
git clone https://github.com/Mu7ammedProg159/Chatcord-Client.git
cd Chatcord-Client
```
2. Start the JavaFX app:
```bash
./mvnw javafx:run
```
3. Make sure the backend is running and reachable.

---

## 💡 Notes

- Token handling is decoupled via a dedicated `TokenManager`
- Real-time messaging leverages an authenticated WebSocket connection
- Shared state and switching handled with JavaFX event bus pattern
- Optimized for maintainability, not just quick prototyping
- Currently supports **Windows OS** with device ID stored in `AppData`. Support for **macOS and Linux** is planned and technically feasible with JavaFX.
- There are more to come, such as Guilds, Servers, Groups, Chatting, roles and many more!
---

## 📄 License

MIT License — (c) 2025 Ninja Battosai

# pa-projeto-1
**Project 1 - Advanced Programming**

**👥 Group 7:**<br>
👨‍💻 Marco Ferreira - 2119022 || GitUsername: raysystems<br>
👨‍💻 Sérgio Olim - 2120322    || GitUsername: SACO5<br>
👨‍💻 Jorge Jesus - 2120022    || GitUsername: jorgejesus03<br>

## 📌 Project Overview
This project implements a parallel web server in Java as part of the "Advanced Programming" course. The server is capable of handling multiple simultaneous requests while ensuring controlled access to shared resources through proper synchronization mechanisms.

### 🛠️ Main Features
- **HTML File Synchronization**: Implemented using a `HashMap` where:
    - **Key**: File path (String)
    - **Value**: `ReentrantLock` to ensure mutual exclusion when accessing HTML files.
- **Thread Pool**: 
    - Handles up to **5 simultaneous requests**.
    - The number of concurrent requests is configurable via `serverConfig.json`.
- **Producer-Consumer Pattern**:
    - Asynchronous logging system using a producer-consumer approach to write requests to the `server.log` file.

### 🚀 Extra Features
- **Caching System (LRU Cache)**: 
    - Implemented to improve performance by reducing disk I/O for frequently accessed HTML files.
    - Cache size is configurable via the `server.config` file.

## 📂 Project Structure
```
├── src
│   ├── main
│   │     ├── java
│   │     │      ├── HTMLSychronization (Makes synchronized access to each .html file)
│   │     │      ├── cache (LRU Cache Implementation)
│   │     │      ├── Logging (Related to utility classes to log things)
│   │     │      └── Utils (Utility classes)
│   │            
└── README.md
└── server.log (Contains server requets logs)
```


## 📑 Configuration
Modify `config/serverConfig.json` to customize:
```json
{
  "port": 8080, (Port that requests will be served)
  "root": "localhost", (Main Address)
  "documentRoot": "html", (Can be compared with public_html on apache/nginx)
  "defaultPage": "index", (Default page name if nothing is specified on route)
  "defaultPageExtension": ".html", (Default page extension if nothing is specified on route)
  "page404": "404.html", (If file not found redirection default page)
  "maximumRequests": 5 (Size of threapool to handle concurrent requests)
}
```

## 📊 Logging Format
Requests are logged asynchronously without blocking requests to `server.log` in JSON format:
```json
{
  "timestamp": "YYYY-MM-DD HH:MM:SS",
  "method": "GET",
  "route": "/user/profile/page.html",
  "origin": "192.168.0.1",
  "response": 200
}
```

## 📜 Authors
- 👨‍💻 Marco Ferreira - 2119022 ([raysystems](https://github.com/raysystems))
- 👨‍💻 Sérgio Olim - 2120322 ([SACO5](https://github.com/SACO5))
- 👨‍💻 Jorge Jesus - 2120022 ([jorgejesus03](https://github.com/jorgejesus03))


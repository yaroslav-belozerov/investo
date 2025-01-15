# Investo
*Your investment companion*
<div style="display: flex; flex-direction: row;">
<img style="width: 33%;" alt="currency ui showcase" src="/misc/screenshots/currencies.png" />
<img style="width: 33%;" alt="shares ui showcase" src="/misc/screenshots/shares.png" />
</div>

## ⏩ Table of contents
- [Status](#project-status)
- [Features](#features)
- [Installation](#installation)

## 👁 Project Status
👷‍♂️ In active development. Not yet released.

## 💡 Features
- [x] Latest information about currency values
- [x] Searching for shares and viewing info about them

## 🏗 Building
1. Get the [T-Invest token](https://tinkoff.github.io/investAPI/token/)
2. Get the project files
   ```sh  
   git clone git@github.com:yaroslav-belozerov/investo.git
   cd ./investo
   ```
   or download as a [zip file](https://github.com/yaroslav-belozerov/investo/archive/refs/heads/main.zip)
### Android
   ```sh
   ./gradlew assembleRelease
   ```
### Desktop
> Only tested environments for desktop are: *Linux (x86_64)*
  ```sh
  ./gradlew createReleaseDistributable
  ```
### Web Server (Kotlin/WASM)
  ```sh
  ./gradlew wasmJsBrowserProductionRun
  ```

## 👥 Contributions
Any suggestions or changes are reviewed via GitHub issues. Please open one if you would like to provide feedback or contribute.

## ⚖ License
The project is licensed under the MIT License. The libraries used are subject to their own licenses.
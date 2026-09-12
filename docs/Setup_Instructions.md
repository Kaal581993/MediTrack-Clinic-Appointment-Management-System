# MediTrack — Java Setup Instructions

This document describes how to install and configure the Java Development Kit (JDK),
verify the runtime environment (JRE / JVM), and build & run the MediTrack project.

---

## 1. Overview

| Component | Description |
|-----------|-------------|
| **JDK** (Java Development Kit) | The full SDK used by developers to compile `.java` source into `.class` bytecode. Includes `javac`, `java`, `javadoc`, and the standard library. |
| **JRE** (Java Runtime Environment) | A subset of the JDK that lets end-users *run* pre-compiled `.class` files. Contains the JVM plus the class libraries. |
| **JVM** (Java Virtual Machine) | The engine that executes bytecode. It is responsible for loading, verifying, interpreting, and (with JIT) compiling code at runtime. |

> **"Write Once, Run Anywhere"** is possible because every platform has its own
> implementation of the JVM. The same `.class` bytecode runs on Windows, macOS, and
> Linux without recompilation.

---

## 2. Install the JDK

### Option A — JDK 17 (used by this project)

The `pom.xml` targets **Java 17** (`<maven.compiler.source>17</maven.compiler.source>`).

#### On Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install openjdk-17-jdk
```

#### On macOS (Homebrew)
```bash
brew install openjdk@17
```

#### On Windows
- Download the installer from [Oracle](https://www.oracle.com/java/technologies/downloads/#java17)
  or use **SDKMAN**: `sdk install java 17.0-open`.

#### On any platform (SDKMAN — recommended)
```bash
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install java 17.0.9-open
```

### Option B — JDK 21 (what this environment uses)
```bash
# SDKMAN
sdk install java 21.0.12-open
# or apt
sudo apt install openjdk-21-jdk
```

---

## 3. Verify the Installation

Open a terminal and run:

```bash
# 1. Which Java compiler / runtime are on PATH?
which javac
which java

# 2. Version & vendor
java -version
javac -version

# 3. Where is the JDK home?
echo $JAVA_HOME          # macOS/Linux
echo %JAVA_HOME%         # Windows (cmd)
```

Expected output (JDK 21):
```
openjdk version "21.0.12.1" 2026-08-18
OpenJDK Runtime Environment (build 21.0.12.1+6-...)
OpenJDK 64-Bit Server VM (build 21.0.12.1+6-..., mixed mode, sharing)
```

### Screenshot guidance (take these manually)
1. `java -version` → shows vendor, version, and JVM build string.
2. `echo %JAVA_HOME%` (Windows) or `echo $JAVA_HOME` (macOS/Linux) → confirms the JDK home path.
3. `javac -version` → confirms the compiler version matches the runtime.

---

## 4. Build the Project

### Without Maven (plain `javac`)
```bash
# Compile every source file into ./out
mkdir -p out
javac -d out $(find src/main/java -name '*.java')

# Run
java -cp out com.airtribe.meditrack.Main
```

### With Maven (recommended)
```bash
# Install Maven: https://maven.apache.org/install.html
# or: sudo apt install maven  (Linux) / brew install maven (macOS)

mvn clean compile
mvn exec:java -Dexec.mainClass="com.airtribe.meditrack.Main"
```

### Command-line arguments
```
java com.airtribe.meditrack.Main --loadData
```
`--loadData` triggers `AppointmentService.loadFromFile()` to restore persisted
appointments from the CSV on startup.

---

## 5. IDE Setup (VS Code)

1. Open the folder `/home/kaal/IdeaProjects/MediTrack-Clinic-Appointment-Management-System`.
2. Install the **Java Extension Pack** (Microsoft) from the Extensions view.
3. Ensure the **Java Language Server** picks up JDK 17/21:
   - `Ctrl+Shift+P` → "Java: Configure Java Runtime" → select a JDK 17+ installation.
4. The project compiles with no warnings under JDK 17/21.

---

## 6. Troubleshooting

| Symptom | Cause | Fix |
|---------|-------|-----|
| `command not found: javac` | JDK not on `PATH` | Add `$JAVA_HOME/bin` to `~/.bashrc` / `~/.zshrc`. |
| `error: release version 17 not supported` | Running an older JDK (e.g. 8) | Install JDK 17+ via SDKMAN or the vendor installer. |
| `class file has wrong version 61.0` | Mixing JDK versions | Rebuild with the same JDK used to run. |
| `Invalid maximum heap size` | Corrupt `JAVA_OPTS` | Unset `JAVA_OPTS` and re-run. |

---

## 7. Environment Summary

| Item | Value |
|------|-------|
| **Language** | Java (object-oriented, platform-independent) |
| **Source level** | Java 17 (also compiles under 21) |
| **Build tool** | Maven 3.x (`pom.xml`) |
| **Main class** | `com.airtribe.meditrack.Main` |
| **Run mode** | Interactive console menu |
| **Persistence** | CSV (`appointments.csv`) via `AppointmentCSVUtil` |

---

*Prepared for the MediTrack Java assignment. Refer to `docs/JVM_Report.md` for the
JVM internals report and `README.md` for the project overview.*

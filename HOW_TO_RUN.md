# How to Run the Voting System Project

## Method 1: Running in Eclipse (Recommended)

### Step 1: Open the Project
1. Open Eclipse IDE
2. The project should already be in your workspace: `eclipse-workspace\miniproject`

### Step 2: Set Up JavaFX (if not already configured)

**Option A: If you have JavaFX as a library:**
1. Right-click on the project → **Properties**
2. Go to **Java Build Path** → **Libraries**
3. Click **Add External JARs** or **Add Library**
4. Add JavaFX SDK libraries (javafx.controls, javafx.graphics, javafx.base)

**Option B: If using Java 11+ with JavaFX modules:**
1. Right-click on the project → **Properties**
2. Go to **Run/Debug Settings**
3. Select or create a run configuration
4. In **Arguments** tab, add VM arguments:
   ```
   --module-path "C:\path\to\javafx-sdk\lib" --add-modules javafx.controls,javafx.graphics,javafx.base
   ```
   (Replace with your actual JavaFX SDK path)

### Step 3: Run the Application

**For the NEW upgraded version:**
1. Navigate to `src/miniproject/VotingApplication.java`
2. Right-click on the file
3. Select **Run As** → **Java Application**
4. The application window should open!

**For the OLD version (hi.java):**
1. Navigate to `src/miniproject/hi.java`
2. Right-click on the file
3. Select **Run As** → **Java Application**

---

## Method 2: Command Line (Windows)

### Prerequisites
- Java 11 or higher installed
- JavaFX SDK downloaded (if using Java 11-16)
- Java 17+ includes JavaFX, but may need separate installation

### Step 1: Open Command Prompt or PowerShell
Navigate to your project directory:
```powershell
cd C:\Users\asus\eclipse-workspace\miniproject
```

### Step 2: Compile the Project

**If using Java 11-16 with separate JavaFX:**
```powershell
javac --module-path "C:\path\to\javafx-sdk\lib" --add-modules javafx.controls,javafx.graphics,javafx.base -d bin src/miniproject/*.java src/module-info.java
```

**If using Java 17+ (check if JavaFX is included):**
```powershell
javac --module-path "C:\path\to\javafx-sdk\lib" --add-modules javafx.controls,javafx.graphics,javafx.base -d bin src/miniproject/*.java src/module-info.java
```

### Step 3: Run the Application

**For the NEW upgraded version:**
```powershell
java --module-path "C:\path\to\javafx-sdk\lib" --add-modules javafx.controls,javafx.graphics,javafx.base -cp bin miniproject.VotingApplication
```

**For the OLD version:**
```powershell
java --module-path "C:\path\to\javafx-sdk\lib" --add-modules javafx.controls,javafx.graphics,javafx.base -cp bin miniproject.hi
```

---

## Method 3: Quick Eclipse Run (If JavaFX is Pre-configured)

1. Open `VotingApplication.java` in Eclipse
2. Click the green **Run** button (▶) in the toolbar
3. Or press **Ctrl+F11**
4. The application should launch!

---

## Troubleshooting

### Error: "JavaFX runtime components are missing"
**Solution:** You need to add JavaFX to your project:
1. Download JavaFX SDK from: https://openjfx.io/
2. Extract it to a folder (e.g., `C:\javafx-sdk-17`)
3. In Eclipse: Right-click project → Properties → Java Build Path → Add External JARs
4. Add all JARs from `javafx-sdk-17\lib\`

### Error: "module not found: javafx.controls"
**Solution:** Add VM arguments in Run Configuration:
```
--module-path "C:\path\to\javafx-sdk\lib" --add-modules javafx.controls,javafx.graphics,javafx.base
```

### Error: "Could not find or load main class"
**Solution:** 
1. Make sure you're running `VotingApplication` (not `hi`)
2. Clean and rebuild: Project → Clean → Clean all projects
3. Refresh the project: Right-click project → Refresh

### Application Window Doesn't Open
**Solution:**
1. Check the Console for error messages
2. Verify JavaFX is properly configured
3. Make sure you're using Java 11 or higher

---

## Quick Start (Eclipse - Simplest Method)

1. **Open Eclipse**
2. **Navigate to:** `src/miniproject/VotingApplication.java`
3. **Right-click** → **Run As** → **Java Application**
4. **Done!** The voting system should open.

If you get JavaFX errors, you'll need to configure JavaFX (see Method 1, Step 2).

---

## What to Expect

When you run the application, you should see:
- A window titled "Voting System - Professional Edition"
- Two tabs: "Vote" and "Admin"
- The "Vote" tab shows candidates and a voting interface
- The "Admin" tab requires password (default: `admin`)

---

## Need Help?

- Check the `README.md` file for more details
- Verify Java version: `java -version` (should be 11+)
- Verify JavaFX installation
- Check Eclipse console for specific error messages


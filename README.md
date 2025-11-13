# Voting System - Professional Edition

A modern, feature-rich JavaFX voting application with a professional user interface and comprehensive administrative controls.

## Features

### Voter Interface
- **Modern UI Design**: Beautiful gradient backgrounds and smooth animations
- **Live Results**: Real-time vote count visualization with interactive bar charts
- **Easy Voting**: Simple candidate selection and one-click voting
- **Vote Confirmation**: Secure confirmation dialog before casting votes
- **Auto-refresh**: Results update automatically when votes are cast

### Admin Interface
- **Secure Authentication**: Password-protected admin access
- **Candidate Management**: Add, remove, and manage candidates
- **Vote Management**: Reset all votes or manage individual candidate data
- **Data Export**: Export results to CSV format
- **Visual Analytics**: Interactive charts and statistics
- **Auto-save**: All changes are automatically saved to disk

### Technical Features
- **Data Persistence**: Automatic saving to `votes.csv` file
- **Modular Architecture**: Clean separation of concerns with dedicated classes
- **Modern JavaFX**: Uses latest JavaFX features and best practices
- **Responsive Design**: Adapts to different window sizes
- **Error Handling**: Comprehensive error handling and user feedback

## Project Structure

```
src/miniproject/
├── VotingApplication.java    # Main application entry point
├── Candidate.java            # Candidate data model
├── VoterPage.java           # Voter interface component
├── AdminPage.java           # Admin interface component
└── VotingDataManager.java   # Data persistence utilities
```

## Getting Started

### Prerequisites
- Java 11 or higher
- JavaFX SDK (included in Java 11+ or download separately)

### Running the Application

1. **Compile the project:**
   ```bash
   javac --module-path <path-to-javafx> --add-modules javafx.controls,javafx.graphics src/miniproject/*.java
   ```

2. **Run the application:**
   ```bash
   java --module-path <path-to-javafx> --add-modules javafx.controls,javafx.graphics -cp src miniproject.VotingApplication
   ```

   Or if using an IDE like Eclipse:
   - Right-click on `VotingApplication.java`
   - Select "Run As" → "Java Application"

### Default Credentials
- **Admin Password**: `admin`

⚠️ **Note**: Change the admin password in production environments!

## Usage

### For Voters
1. Navigate to the "Vote" tab
2. Select a candidate from the list
3. Click "Vote Now"
4. Confirm your selection
5. View live results on the right side

### For Administrators
1. Navigate to the "Admin" tab
2. Enter the admin password (default: `admin`)
3. Click "Login"
4. Manage candidates, view statistics, or export data
5. Click "Logout" when done

## Data Management

- **Auto-save**: All changes are automatically saved to `votes.csv`
- **Manual Export**: Use "Export CSV" button in admin panel to save results to a custom location
- **Data Format**: CSV file with format: `Name,Votes`

## Architecture

The application follows a modular architecture:

- **VotingApplication**: Main application class managing the overall application lifecycle
- **VoterPage**: Handles all voter-facing functionality
- **AdminPage**: Manages administrative features and authentication
- **VotingDataManager**: Handles all data persistence operations
- **Candidate**: Data model representing a voting candidate

## Customization

### Changing Admin Password
Edit the `ADMIN_PASSWORD` constant in `VotingApplication.java`:
```java
private static final String ADMIN_PASSWORD = "your-password-here";
```

### Styling
The application uses inline CSS styles. Modify the style strings in:
- `VoterPage.java` for voter interface styling
- `AdminPage.java` for admin interface styling
- `VotingApplication.java` for global application styling

## Future Enhancements

Potential improvements:
- User authentication system
- Vote history and audit logs
- Multiple election support
- Real-time network synchronization
- Advanced analytics and reporting
- Email notifications
- Mobile-responsive design

## License

This project is provided as-is for educational and development purposes.

## Support

For issues or questions, please refer to the code documentation or contact the development team.


# qtGUI - Quality Threshold Clustering GUI

JavaFX graphical user interface for the Quality Threshold Clustering algorithm.

## Overview

qtGUI is a modern, user-friendly desktop application that provides a graphical interface for executing and visualizing Quality Threshold clustering operations. Built with JavaFX 21, it offers an intuitive workflow from dataset selection to result visualization.

## Features

### Implemented (Sprint 0 & 1)

- Modern JavaFX-based user interface
- Multi-view navigation system (Home, Clustering, Results, Settings)
- Dataset selection (Hardcoded, CSV, Database)
- Clustering parameter configuration with validation
- Real-time progress feedback during clustering
- Results visualization with cluster tree view
- Configurable application settings
- Responsive and polished UI with custom CSS styling

### Planned (Future Sprints)

- Backend integration with qtServer (Sprint 2)
- 2D scatter plot visualization (Sprint 3)
- Export functionality (CSV, PDF, PNG) (Sprint 4)
- Save/Load clustering results (.dmp files) (Sprint 4)
- Statistics dashboard (Sprint 4)
- 3D visualization (Sprint 4 - optional)

## Requirements

- Java 21 or higher
- Maven 3.9+
- JavaFX 21+
- Internet connection (for downloading Maven dependencies)

## Project Structure

```
qtGUI/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── gui/
│   │   │   │   ├── MainApp.java              # Application entry point
│   │   │   │   ├── controllers/
│   │   │   │   │   ├── MainController.java   # Main window controller
│   │   │   │   │   ├── HomeController.java   # Dataset selection
│   │   │   │   │   ├── ClusteringController.java  # Progress feedback
│   │   │   │   │   ├── ResultsController.java     # Results display
│   │   │   │   │   └── SettingsController.java    # Configuration
│   │   │   │   ├── models/                   # Data models (future)
│   │   │   │   ├── services/                 # Business logic (future)
│   │   │   │   ├── charts/                   # Visualization (future)
│   │   │   │   └── utils/                    # Utilities (future)
│   │   │   └── module-info.java              # Java module descriptor
│   │   └── resources/
│   │       ├── views/
│   │       │   ├── main.fxml                 # Main layout
│   │       │   ├── home.fxml                 # Home view
│   │       │   ├── clustering.fxml           # Clustering view
│   │       │   ├── results.fxml              # Results view
│   │       │   └── settings.fxml             # Settings view
│   │       ├── styles/
│   │       │   └── application.css           # Application stylesheet
│   │       └── icons/                        # Icons (future)
│   └── test/
│       └── java/                             # Unit tests (future)
├── pom.xml                                   # Maven configuration
└── README.md                                 # This file
```

## Build and Run

### Prerequisites

Ensure Maven and Java 21 are installed:

```bash
java --version
# Should show Java 21 or higher

mvn --version
# Should show Maven 3.9+
```

### Build the Project

```bash
cd qtGUI
mvn clean compile
```

### Run the Application

```bash
mvn javafx:run
```

### Package as JAR

```bash
mvn clean package
```

This creates an executable JAR in `target/qtGUI-1.0.0.jar`.

### Run the JAR

```bash
java -jar target/qtGUI-1.0.0.jar
```

## Usage

### 1. Home View - Dataset Selection

- Select data source: Hardcoded (PlayTennis), CSV File, or Database
- Configure clustering radius (e.g., 0.5)
- Enable/disable distance caching
- Click "Start Clustering" to begin

### 2. Clustering View - Progress Monitoring

- Real-time progress bar
- Current step information
- Clusters found counter
- Tuples clustered counter
- Elapsed time tracking
- Activity log with detailed progress

### 3. Results View - Cluster Analysis

- Tree view with all clusters
- Cluster details (centroid, size, average distance)
- Tuple listing for each cluster
- Statistics for each cluster
- Export and save options (future)

### 4. Settings View - Configuration

- Appearance (theme, font size)
- Performance (caching, threads, memory)
- Clustering defaults (radius, data source)
- Export settings (format, directory)
- Database configuration

## Navigation

- **File Menu**:
  - New Analysis: Start a new clustering session
  - Open: Load saved clustering (future)
  - Save / Save As: Save clustering results (future)
  - Exit: Close application

- **Edit Menu**:
  - Settings: Configure application preferences

- **View Menu**:
  - Show/Hide Toolbar
  - Show/Hide Status Bar

- **Help Menu**:
  - Documentation
  - About

## Keyboard Shortcuts

- `Ctrl+N`: New Analysis
- `Ctrl+O`: Open
- `Ctrl+S`: Save
- `Ctrl+E`: Export (future)
- `F5`: Refresh (future)

## Configuration

Settings are saved in `qtgui.properties` in the application directory.

Default values:
- Theme: Light
- Font Size: Medium (14px)
- Radius: 0.5
- Caching: Enabled
- Thread Pool Size: 4
- Memory Limit: 512 MB

## Dependencies

- JavaFX 21.0.1 (Controls, FXML)
- XChart 3.8.5 (Charting - future use)
- ControlsFX 11.2.0 (UI enhancements)
- SLF4J 2.0.9 + Logback 1.4.14 (Logging)
- JUnit 5.10.1 + TestFX 4.0.18 (Testing - future)

## Development Status

### Completed

- [x] Sprint 0: Setup and configuration
  - [x] Maven project structure
  - [x] JavaFX dependencies
  - [x] Module configuration (module-info.java)
  - [x] Directory structure

- [x] Sprint 1: UI Base and Navigation
  - [x] Main window layout (MenuBar, ToolBar, StatusBar)
  - [x] Home view with dataset selection
  - [x] Clustering view with progress feedback
  - [x] Results view with cluster tree
  - [x] Settings view with configuration
  - [x] Navigation system
  - [x] CSS styling

### In Progress / Planned

- [ ] Sprint 2: Backend Integration
  - [ ] ClusteringService wrapper for QTMiner
  - [ ] DataImportService (CSV, Database)
  - [ ] JavaFX Task for async clustering
  - [ ] Error handling and logging

- [ ] Sprint 3: 2D Visualization
  - [ ] Scatter plot implementation
  - [ ] PCA for dimensionality reduction
  - [ ] Interactive chart (zoom, pan, tooltip)
  - [ ] Chart export (PNG, SVG)

- [ ] Sprint 4: Advanced Features
  - [ ] Save/Load clustering (.dmp files)
  - [ ] Export results (CSV, PDF)
  - [ ] Statistics dashboard
  - [ ] Cluster comparison
  - [ ] 3D visualization (optional)
  - [ ] Dark mode theme

- [ ] Sprint 5: Testing and Deployment
  - [ ] Unit tests
  - [ ] Integration tests
  - [ ] Cross-platform testing
  - [ ] Native installers (jpackage)

## Known Issues

- Maven build requires internet connection to download dependencies
- Clustering functionality is currently simulated (Sprint 1)
- Backend integration with qtServer pending (Sprint 2)
- Chart visualization pending (Sprint 3)

## Troubleshooting

### Maven Cannot Download Dependencies

If you see network errors during `mvn compile`:
- Ensure you have internet connectivity
- Check your firewall/proxy settings
- Verify Maven repository access: https://repo.maven.apache.org/maven2

### JavaFX Runtime Error

If you see "Error: JavaFX runtime components are missing":
- Ensure JavaFX dependencies are correctly configured in pom.xml
- Run via `mvn javafx:run` instead of direct `java` command
- Verify Java 21+ is being used

### FXML Loading Failed

If views don't load:
- Check that FXML files are in `src/main/resources/views/`
- Verify controller class names in FXML match actual classes
- Check module-info.java exports and opens statements

## Contributing

This is an academic project for the MAP (Metodi Avanzati di Programmazione) course.

### Code Style

- Follow Java naming conventions
- Use Javadoc for public methods
- Keep controllers focused on UI logic
- Separate business logic into services

### Commit Messages

- Use clear, descriptive commit messages
- Reference sprint and task numbers
- Example: "Sprint 1.3: Add home view with dataset selection"

## License

Academic project for MAP course.

## Authors

- Developed with assistance from Claude (AI Assistant)
- Course: Metodi Avanzati di Programmazione (MAP)
- Year: 2025

## Version History

- **v1.0.0** (2025-11-08): Sprint 0 & 1 completed
  - Initial JavaFX setup
  - Complete UI base with all views
  - Navigation system implemented

## Support

For issues related to:
- qtServer integration: See `../qtServer/README.md`
- General project setup: See `../README.md`
- Roadmap and planning: See `../QTGUI_ROADMAP.md`

## Next Steps

1. Complete Sprint 2: Integrate with qtServer backend
2. Implement ClusteringService to actually execute QTMiner
3. Add DataImportService for CSV and Database loading
4. Test end-to-end clustering workflow

---

**Last Updated**: 2025-11-08
**Current Sprint**: 1 (Completed)
**Next Sprint**: 2 (Backend Integration)

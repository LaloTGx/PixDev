# PixDev
PixDev is an Android application for project and requirement management, designed to help developers organize work through structured prioritization and progress tracking.
Built with a pixel-inspired visual identity, the app combines modern Android development practices with a focused productivity workflow.

## Features
- Project creation and management
- Requirement tracking per project
- Priority classification:
  - Essential
  - Mandatory
  - Optional
- Completion progress calculation
- Pin important projects
- Sorting by:
  - Date
  - Name
  - Priority
- Dynamic theme adaptation
- Localization support
- Responsive typography for different screen sizes
- Local data persistence

## Architecture
PixDev follows the **MVVM** architecture pattern with clear separation between UI, business logic, and data handling.

### Components
**ProjectViewModel**

Handles:

- Project creation
- Project deletion
- Pinning logic
- Project validation rules

**RequirementViewModel**
Handles:

- Requirement management
- Sorting and filtering
- Progress updates

## Stack
- **Kotlin**
- **Jetpack Compose**
- **Material 3**
- **Hilt**
- **Room**
- **Repository Pattern**
- **Adaptive UI System**

## Project Constraints
PixDev enforces workflow limits to encourage focus:

- Maximum **5 projects**
- Maximum **3 pinned projects**

These constraints are intentional design decisions aimed at reducing overload and improving task prioritization.

## Design Philosophy
The application uses a pixel-inspired interface designed around:

- Visual clarity
- Structured productivity
- Lightweight interaction patterns
- Developer-oriented usability

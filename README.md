# The Manny Hub

**The Manny Hub** is a comprehensive JavaFX-based desktop application designed for tailoring businesses to manage customers, garments, and measurements efficiently.

## Overview

The Manny Hub provides a complete solution for tailors and garment makers to:
- Manage customer profiles with detailed measurement data
- Track different types of garments (Suits, Jackets, Shirts, Trousers)
- Maintain order history and status
- Secure authentication with role-based access control
- View analytics through an interactive dashboard

## Features

### Customer Management
- Add, edit, and delete customer profiles
- Store comprehensive body measurements:
  - Waist, hip, thigh dimensions
  - Inseam and rise measurements
  - Fit preferences and special notes
- Track customer status (Active, Inactive, etc.)

### Garment Management
- Create and manage multiple garment types:
  - **Suits** - Complete suit measurements
  - **Jackets** - Jacket-specific measurements
  - **Shirts** - Shirt measurements and details
  - **Trousers** - Pants/trouser measurements
- Validate measurements before saving
- Associate garments with specific customers
- Track creation and modification dates
- Add custom notes for each garment

### User Authentication
- Secure login system with hashed passwords
- Role-based access:
  - **ADMIN** - Full system access
  - **TAILOR** - Standard user access
- Session management with automatic logout

### Dashboard
- Overview of total customers and garments
- Quick access to recent activity
- Visual analytics and statistics

## Technology Stack

- **Language**: Java 21
- **Framework**: JavaFX 21.0.6
- **Build Tool**: Maven
- **Data Storage**: JSON files (customers.json, users.json, garments.json)
- **UI**: FXML-based with CSS styling
- **Libraries**:
  - org.json - JSON parsing
  - ControlsFX, FormsFX - Enhanced UI components
  - BootstrapFX - Modern styling
  - ValidatorFX - Form validation

## Project Structure

```
TheMannyHub/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/themannyhub/
│   │   │       ├── controllers/        # View controllers
│   │   │       ├── models/            # Data models
│   │   │       ├── services/          # Business logic services
│   │   │       ├── data/              # Data access objects (DAO)
│   │   │       └── utils/             # Utility classes
│   │   └── resources/
│   │       └── com/example/themannyhub/  # FXML files
│   └── test/                          # Unit tests
├── pom.xml                            # Maven configuration
├── customers.json                     # Customer data storage
└── users.json                         # User account data
```

## Getting Started

### Prerequisites

- Java JDK 21 or later
- Maven 3.8.6 or later
- Git (optional)

### Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/your-repo/TheMannyHub.git
   cd TheMannyHub
   ```

2. **Install dependencies:**
   ```bash
   mvn clean install
   ```

3. **Run the application:**
   ```bash
   mvn clean javafx:run
   ```

   Or use the Maven wrapper:
   ```bash
   ./mvnw clean javafx:run
   ```

### Building for Distribution

To create a self-contained package:
```bash
mvn clean package
```

This generates a distributable package in the `target/` directory.

## Usage

### First Run

1. The application starts with a login screen
2. Use the default admin credentials (check users.json) or create a new user
3. After successful login, the main window opens with dashboard view

### Managing Customers

1. Navigate to the Customers section
2. Click "Add Customer" to create a new profile
3. Enter all required measurements and preferences
4. Save to persist the customer data

### Managing Garments

1. Select a customer from the list
2. Click "Add Garment" and choose the type (Suit, Jacket, Shirt, Trousers)
3. Enter the specific measurements for the garment type
4. Save to associate the garment with the customer

### Dashboard

The dashboard provides:
- Total count of customers and garments
- Recent activity overview
- Quick navigation to all features

## Architecture

### Model-View-Controller (MVC) Pattern

- **Models**: Data classes representing Customer, User, Garment, and their variants
- **Views**: FXML files defining the UI layout
- **Controllers**: Handle user interactions and connect views to models
- **Services**: Business logic layer (AuthService, CustomerService, GarmentService)
- **DAO**: Data Access Objects for persistence (CustomerDAO, GarmentDAO, UserDAO)

### Polymorphism in Design

The Garment class hierarchy demonstrates OOP principles:
- `Garment` - Abstract base class with common properties
- `SuitMeasurements` - Suit-specific implementation
- `JacketMeasurements` - Jacket-specific implementation
- `ShirtMeasurements` - Shirt-specific implementation
- `TrouserMeasurements` - Trousers-specific implementation

Each concrete garment type:
- Implements abstract methods (`validate()`, `getMeasurementSummary()`, `getRequiredMeasurements()`)
- Extends base functionality with type-specific measurements
- Maintains its own validation rules

### Data Persistence

All data is stored in JSON files:
- `customers.json` - Customer profiles and measurements
- `users.json` - User accounts and authentication data
- `garments.json` - All garment records

The DAO layer handles:
- Loading data on application start
- Saving data on changes
- Generating next IDs automatically

## Configuration

### Maven Configuration

The `pom.xml` file contains all dependencies and build configuration. Key properties:
- Java source/target: 21
- JavaFX version: 21.0.6
- JSON library: org.json 20240303

### Application Configuration

Edit the following files to customize the application:
- `src/main/java/module-info.java` - Module declarations
- FXML files in `src/main/resources/` - UI layouts
- CSS files - Styling and themes

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Make your changes
4. Run tests: `mvn test`
5. Commit your changes (`git commit -m 'Add amazing feature'`)
6. Push to the branch (`git push origin feature/amazing-feature`)
7. Open a Pull Request

## Code Quality

- Follow Java naming conventions
- Add Javadoc comments for public methods
- Validate all user inputs
- Use meaningful variable and method names
- Keep methods focused and single-purpose

## Troubleshooting

### Common Issues

**Application won't start:**
- Ensure Java 21 is installed: `java -version`
- Verify Maven is installed: `mvn -version`
- Run with more memory: `mvn -X clean javafx:run`

**JavaFX modules not found:**
- Ensure all JavaFX modules are included in `module-info.java`
- Check Maven dependencies in `pom.xml`

**Data not saving:**
- Check file permissions on JSON data files
- Verify the application has write access to the directory

### Logging

The application outputs status messages to the console:
- Service initialization logs
- Data loading/saving notifications
- Authentication events

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contact

For questions or support, please contact the development team.

---

**The Manny Hub** - Tailoring Management Made Easy

*Version 1.0-SNAPSHOT* | *Java 21* | *JavaFX 21.0.6*

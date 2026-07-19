```markdown
# tyson Development Patterns

> Auto-generated skill from repository analysis

## Overview
This skill teaches best practices and conventions for developing in the `tyson` Kotlin codebase. It covers file organization, code style, import/export patterns, and testing approaches. While no specific frameworks or automated workflows are detected, this guide helps maintain consistency and efficiency across the project.

## Coding Conventions

### File Naming
- Use **PascalCase** for all file names.
  - **Example:**  
    `MyAwesomeClass.kt`

### Import Style
- Use **relative imports** to reference other files or modules within the project.
  - **Example:**
    ```kotlin
    import mypackage.subpackage.MyClass
    ```

### Export Style
- Use **named exports** for classes, functions, or objects.
  - **Example:**
    ```kotlin
    // MyAwesomeClass.kt
    class MyAwesomeClass {
        // ...
    }
    ```

### Commit Messages
- Commit messages are freeform, with no enforced prefix.
- Average commit message length is 46 characters.
  - **Example:**
    ```
    Fix bug in user authentication logic
    ```

## Workflows

### Adding a New Feature
**Trigger:** When you need to implement a new feature.
**Command:** `/add-feature`

1. Create a new Kotlin file using PascalCase for the filename.
2. Implement your feature using named exports.
3. Use relative imports to include dependencies.
4. Write corresponding tests in a file matching `*.test.*`.
5. Commit your changes with a clear, concise message.

### Refactoring Code
**Trigger:** When improving or restructuring existing code.
**Command:** `/refactor-code`

1. Identify the code to refactor.
2. Update file and class names to follow PascalCase if needed.
3. Adjust imports to remain relative.
4. Ensure all exports remain named.
5. Update or add tests as necessary.
6. Commit with a descriptive message.

### Writing Tests
**Trigger:** When adding or updating tests for your code.
**Command:** `/write-test`

1. Create a test file matching the pattern `*.test.*`.
2. Implement test cases for your feature or module.
3. Use the project's preferred (unknown) testing framework.
4. Run tests to verify correctness.
5. Commit test files with a clear message.

## Testing Patterns

- Test files follow the pattern: `*.test.*`
  - **Example:**  
    `UserService.test.kt`
- The specific testing framework is not detected; use standard Kotlin testing practices.
- Place test files alongside or near the code they test for clarity.

## Commands
| Command         | Purpose                                 |
|-----------------|-----------------------------------------|
| /add-feature    | Scaffold and implement a new feature    |
| /refactor-code  | Refactor existing code for improvements |
| /write-test     | Add or update tests for your code       |
```
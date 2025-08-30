# Testing Guide

This document provides an overview of the testing strategy and instructions for running tests in the FPL-BR Pilot frontend application.

## Test Types

1. **Unit Tests**: Test individual components and functions in isolation.
2. **Integration Tests**: Test the interaction between components.
3. **End-to-End (E2E) Tests**: Test complete user flows using Playwright.
4. **Visual Regression Tests**: Ensure UI consistency using Loki and Storybook.
5. **Component Tests**: Test components in isolation using Storybook stories.

## Running Tests

### Prerequisites

- Node.js 20.x or later
- npm 9.x or later
- Chrome/Firefox/WebKit (for E2E tests)

### Install Dependencies

```bash
npm install
```

### Unit & Integration Tests

Run all unit and integration tests:

```bash
npm test
```

Run tests in watch mode:

```bash
npm run test:watch
```

Run tests with coverage:

```bash
npm run test:coverage
```

### End-to-End Tests

Run all E2E tests:

```bash
npm run test:e2e
```

Run E2E tests with UI mode:

```bash
npm run test:e2e:ui
```

View E2E test report:

```bash
npm run test:e2e:report
```

### Visual Regression Tests

Run visual regression tests:

```bash
# Build Storybook and run tests
npm run storybook:build
npm run test:visual
```

Approve visual changes:

```bash
npm run test:visual:approve
```

Update reference images:

```bash
npm run test:visual:update
```

### Run All Tests

Run all test suites in sequence:

```bash
npm run test:all
```

## CI/CD Pipeline

The project includes a GitHub Actions workflow (`.github/workflows/ci-cd.yml`) that runs on every push and pull request. The pipeline includes:

1. Linting
2. Unit & integration tests with coverage
3. Build verification
4. Visual regression tests
5. E2E tests
6. Deployment to production (on main branch)

## Test Structure

- `src/__tests__/`: Unit and integration tests
- `e2e/`: End-to-end tests (Playwright)
- `.storybook/`: Storybook configuration
- `stories/`: Component stories for visual testing
- `.loki/`: Visual regression test configuration

## Writing Tests

### Unit/Integration Tests

- Use `vitest` for test runner
- Use `@testing-library/react` for component testing
- Place test files next to the code they test with `.test.tsx` or `.test.ts` extension
- Use MSW (Mock Service Worker) for API mocking

### E2E Tests

- Use Playwright for browser automation
- Test critical user flows
- Use page object pattern for better maintainability

### Visual Tests

- Create stories in Storybook for all components
- Use the `loki` CLI to manage visual regression tests
- Update reference images when intentional UI changes are made

## Debugging Tests

### Debugging Unit Tests

Run tests with debug output:

```bash
npm test -- --debug
```

### Debugging E2E Tests

Run tests in headed mode:

```bash
npx playwright test --headed
```

Use Playwright Inspector:

```bash
PWDEBUG=1 npx playwright test
```

## Test Coverage

View coverage report:

```bash
npm run test:coverage
```

The coverage report will be available in the `coverage/` directory.

## Best Practices

1. Write tests that resemble how your software is used
2. Test behavior, not implementation details
3. Keep tests focused and independent
4. Use descriptive test names
5. Mock external dependencies
6. Keep tests maintainable and readable
7. Run tests frequently during development
8. Fix failing tests immediately

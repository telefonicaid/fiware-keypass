# Behavior tests
## Overview

The Keypass is developed using Behavior Driven Development, using [Lettuce](https://github.com/gabrielfalcao/lettuce) (Cucumber port to Python). All the tests are black box end to end tests of the whole component, aimed to document the API behavior as well as to test it.

## Setup
In order to setup the environment to execute the tests, follow this steps:

1. Create a virtual environment for the tests.

   ```
   virtualenv VENV
   ```

2. Activate the environment.

   ```
   source VENV/bin/activate
   ```

3. Install the requirements using PIP.

   ```
   pip install -r requirements.txt
   ```

With this steps, all the requirements will be installed in your own environment.

## Execution

To execute the tests, from the `src/behavior` folder use:

```
lettuce
```

This will execute all the tests in the suite.

## How to add test suites

To add test suites, simply create a new `.feature` file in the `features/` folder following the [Gherkin grammar](https://github.com/cucumber/cucumber/wiki/Gherkin). If the steps defined in the new file are present in any step files, 
they will be reused. If there are new tests, just implement them in the appropriate folders.


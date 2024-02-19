# CMRIT 2025 Batch AutoUpdating Leaderboard.

**The autoupdating leaderboard of CMR Institute of Technology, 2025 batch.**

**The scores of CMRIT 2025 Batch will automatically be mapped at https://gabyah92.github.io/CMRIT2025Leaderboard/ based on usernames from https://tinyurl.com/2025-CODING-USERNAMES**

<<<<<<< HEAD
**Many thanks to [Rushyendra(21r01a67e6)](https://github.com/dog-broad) for his contributions on this projects UI, Yaml, gradle build, code, and so much more!**

## Introduction:
This project aims to create and maintain a leaderboard for participants from CMR Institute of Technology (CMRIT) for the year 2025. The leaderboard displays ratings and statistics of participants from various coding platforms such as Codeforces, LeetCode, GeeksforGeeks, Codechef, and Hackerrank.

## Features:
- **Leaderboard Generation:** The project scrapes data from different coding platforms and processes it to generate a leaderboard showcasing participants' ratings and percentiles.
- **Automation:** GitHub Actions workflows automate the build process, run tests, and handle other tasks such as artifact uploading and downloading.
- **Web Interface:** A web interface is provided for easy navigation and visualization of the leaderboard data.
- **Data Handling:** Participant details are stored in a SQLite database and loaded from a CSV file. The project also checks the existence of URLs associated with participant handles.
- **Documentation:** Comprehensive docstrings and comments are provided in the code for better understanding and maintainability.

## Project Structure:
- **src/main/java/org/cmrit/:** Contains Java source files responsible for scraping data from coding platforms and generating the leaderboard.
- **usernameVerifier/main.py:** Python script to handle participant data loading from an Excel sheet, check URL existence, and write details to CSV and log files.
- **.github/workflows/:** Contains GitHub Actions workflows for automated build and testing processes.
- **src/main/resources/:** Includes HTML files for the web interface and other resources used in the project.

## Setup:
1. **Clone the Repository:** Use `git clone` to clone the repository to your local machine.
2. **Gradle Installation:** Ensure you have Gradle installed on your system.
3. **Dependencies Installation:** Run `gradle build` to download and install the required dependencies.
4. **Execution:** Use Gradle to run the main Java files (`CMRITLeaderboard2025.java`) to generate the leaderboard.

## Usage:
- **Generating Leaderboard:** Execute the main Java files using Gradle to scrape data from coding platforms, process it, and generate the leaderboard.
- **Web Interface:** Access the web interface to view and interact with the leaderboard.
- **Data Handling:** Use the Python script `main.py` to load participant data from an Excel sheet, check URL existence, and manage details.

## Contributing:
Contributions are welcome! Feel free to submit issues for bug fixes, feature requests, or improvements. Pull requests are also appreciated for implementing new features or fixing existing issues.

## Acknowledgments:
- This project is inspired by the need to showcase the coding prowess of CMRIT students and provide a platform for recognition.

## Disclaimer:
This project is not officially affiliated with CMR Institute of Technology (CMRIT). It is an independent initiative by coding enthusiasts.
=======
**Many thanks to [Rushyendra(21r01a67e6)](https://github.com/dog-broad) for his contributions on this projects UI and Yaml!**
>>>>>>> parent of 725aa51 (Merge branch 'pr/3')
